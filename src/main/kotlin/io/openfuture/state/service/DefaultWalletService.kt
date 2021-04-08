package io.openfuture.state.service

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.blockchain.dto.UnifiedTransaction
import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WalletIdentity
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.TransactionRepository
import io.openfuture.state.repository.WalletRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service

@Service
class DefaultWalletService(
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository,
    private val webhookService: WebhookService
) : WalletService {

    override suspend fun findByIdentity(blockchain: String, address: String): Wallet {
        val identity = WalletIdentity(blockchain, address)
        return walletRepository.findByIdentity(identity).awaitFirstOrNull()
            ?: throw NotFoundException("Wallet not found: $blockchain - $address")
    }

    override suspend fun findById(id: String): Wallet {
        return walletRepository.findById(id).awaitFirstOrNull()
            ?: throw NotFoundException("Wallet not found: $id")
    }

    override suspend fun save(blockchain: Blockchain, address: String, webhook: String): Wallet {
        val wallet = Wallet(WalletIdentity(blockchain.getName(), address), webhook)
        return walletRepository.save(wallet).awaitSingle()
    }

    override suspend fun addTransactions(blockchain: Blockchain, block: UnifiedBlock) {
        for (transaction in block.transactions) {
            val identity = WalletIdentity(blockchain.getName(), transaction.to)
            val wallet = walletRepository.findByIdentity(identity).awaitFirstOrNull()

            wallet?.let { saveTransaction(it, block, transaction) }
        }
    }

    private suspend fun saveTransaction(wallet: Wallet, block: UnifiedBlock, unifiedTransaction: UnifiedTransaction) {
        val transaction = Transaction(
            wallet.identity,
            unifiedTransaction.hash,
            unifiedTransaction.from,
            unifiedTransaction.to,
            unifiedTransaction.amount,
            block.date,
            block.number,
            block.hash
        )

        transactionRepository.save(transaction).awaitSingle()
        webhookService.scheduleTransaction(wallet, transaction)
    }

}
