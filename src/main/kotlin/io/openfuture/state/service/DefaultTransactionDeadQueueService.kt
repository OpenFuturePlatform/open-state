package io.openfuture.state.service

import io.openfuture.state.domain.TransactionDeadQueue
import io.openfuture.state.domain.TransactionQueueTask
import io.openfuture.state.domain.WalletIdentity
import io.openfuture.state.repository.TransactionDeadQueueRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service

@Service
class DefaultTransactionDeadQueueService(
    private val repository: TransactionDeadQueueRepository
) : TransactionDeadQueueService {

    override suspend fun addTransactionToDeadQueue(
        walletIdentity: WalletIdentity,
        transactions: List<TransactionQueueTask>
    ): TransactionDeadQueue {
        val transactionsDeadQueue = repository.findByWalletIdentity(walletIdentity).awaitFirstOrNull()
            ?: TransactionDeadQueue(walletIdentity)

        transactionsDeadQueue.addTransactions(transactions)
        return transactionsDeadQueue
    }

    override suspend fun getTransactionFromDeadQueue(walletIdentity: WalletIdentity): List<TransactionQueueTask> {
        if (hasTransactions(walletIdentity)) {
            val transactionsDeadQueue = repository.findByWalletIdentity(walletIdentity).awaitSingle()
            return transactionsDeadQueue.getTransactions()
        }

        return emptyList()
    }

    override suspend fun hasTransactions(walletIdentity: WalletIdentity): Boolean {
        return repository.existsByWalletIdentity(walletIdentity).awaitSingle()
    }

    override suspend fun removeFromDeadQueue(walletIdentity: WalletIdentity) {
        repository.deleteByWalletIdentity(walletIdentity)
    }

}
