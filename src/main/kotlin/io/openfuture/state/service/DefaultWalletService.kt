package io.openfuture.state.service

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.blockchain.dto.UnifiedTransaction
import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.WalletRepository
import io.openfuture.state.webhook.WebhookStatus
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service

@Service
class DefaultWalletService(
        private val repository: WalletRepository,
        private val transactionService: TransactionService,
        private val webhookService: WebhookService
) : WalletService {

    override suspend fun save(blockchain: Blockchain, address: String, webhook: String): Wallet {
        val wallet = Wallet(blockchain.getName(), address, webhook)
        return repository.save(wallet).awaitSingle()
    }

    override suspend fun save(wallet: Wallet): Wallet {
        return repository.save(wallet).awaitSingle()
    }

    override suspend fun findByBlockchainAndAddress(blockchain: String, address: String): Wallet {
        return repository.findByBlockchainAndAddress(blockchain, address)
                .awaitFirstOrNull() ?:
                throw NotFoundException("Wallet not found: $blockchain - $address")
    }

    override suspend fun update(walletId: String, webhook: String): Wallet {
        val wallet = repository.findById(walletId).awaitFirstOrNull() ?: throw NotFoundException("Wallet not found")
        if (webhook != wallet.webhook) {
            wallet.let {
                it.webhookStatus = WebhookStatus.OK
                it.webhook = webhook
            }
        }

        repository.save(wallet).awaitSingle()
        webhookService.addTransactionsFromDeadQueue(wallet)

        return wallet
    }

    override suspend fun addTransactions(blockchain: Blockchain, block: UnifiedBlock) {
        for (transaction in block.transactions) {
            val wallet = repository.findByBlockchainAndAddress(blockchain.getName(), transaction.to).awaitFirstOrNull()

            wallet?.let { saveTransaction(it, block, transaction) }
        }
    }

    private suspend fun saveTransaction(wallet: Wallet, block: UnifiedBlock, unifiedTransaction: UnifiedTransaction) {
        var transaction = Transaction(
                unifiedTransaction.hash,
                unifiedTransaction.from,
                unifiedTransaction.to,
                unifiedTransaction.amount,
                block.date,
                block.number,
                block.hash
        )

        transaction = transactionService.save(transaction)
        wallet.addTransaction(transaction)
        repository.save(wallet).awaitSingle()

        webhookService.addTransaction(wallet, transaction)
    }
}
