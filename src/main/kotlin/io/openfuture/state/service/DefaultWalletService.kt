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
        private val webhookService: WebhookService
) : WalletService {

    override suspend fun save(blockchain: Blockchain, address: String, webhook: String): Wallet {
        val wallet = Wallet(blockchain.getName(), address, webhook)
        return repository.save(wallet).awaitSingle()
    }

    override suspend fun update(walletId: String, webhook: String): Wallet {
        val wallet = repository.findById(walletId).awaitFirstOrNull() ?: throw NotFoundException("Wallet not found")
        if (webhook != wallet.webhook) wallet.webhookStatus = WebhookStatus.OK
        wallet.webhook = webhook
        return repository.save(wallet).awaitSingle()
    }

    override suspend fun findByAddress(address: String): Wallet {
        return repository.findByAddress(address).awaitFirstOrNull()
                ?: throw NotFoundException("Wallet not found")
    }

    override suspend fun addTransactions(blockchain: Blockchain, block: UnifiedBlock) {
        for (transaction in block.transactions) {
            val wallet = repository.findByBlockchainAndAddress(blockchain.getName(), transaction.to).awaitFirstOrNull()

            wallet?.let { saveTransaction(it, block, transaction) }
        }
    }

    private suspend fun saveTransaction(wallet: Wallet, block: UnifiedBlock, unifiedTransaction: UnifiedTransaction) {
        val transaction = Transaction(
                unifiedTransaction.hash,
                unifiedTransaction.from,
                unifiedTransaction.to,
                unifiedTransaction.amount,
                block.date,
                block.number,
                block.hash
        )

        wallet.addTransaction(transaction)
        repository.save(wallet).awaitSingle()
        webhookService.queueWebhook(wallet, transaction)
    }
}
