package io.openfuture.state.service

import io.openfuture.state.domain.TransactionQueueTask
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.TransactionQueueRedisRepository
import kotlinx.coroutines.reactive.awaitFirstOrDefault
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Service

@Service
class DefaultTransactionsQueueService(
        private val repository: TransactionQueueRedisRepository
): TransactionsQueueService {

    override suspend fun add(walletId: String, transaction: TransactionQueueTask) {
        repository.add(walletId, transaction)
    }

    override suspend fun first(walletId: String): TransactionQueueTask {
        return repository.first(walletId)
                .awaitFirstOrNull()
                ?: throw NotFoundException("Transaction not found")
    }

    override suspend fun remove(walletId: String) {
        repository.remove(walletId)
    }

    override suspend fun setAt(walletId: String, transaction: TransactionQueueTask, index: Long) {
        repository.setAtPosition(walletId, transaction, index)
    }

    override suspend fun hasTransactions(walletId: String): Boolean {
        val count = repository.count(walletId)
                .awaitFirstOrDefault(0)

        return count > 0
    }
}
