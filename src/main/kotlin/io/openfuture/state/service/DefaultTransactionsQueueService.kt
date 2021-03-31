package io.openfuture.state.service

import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.TransactionsRedisRepository
import io.openfuture.state.webhook.ScheduledTransaction
import kotlinx.coroutines.reactive.awaitFirstOrDefault
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Service

@Service
class DefaultTransactionsQueueService(
        private val repository: TransactionsRedisRepository
): TransactionsQueueService {

    override suspend fun add(walletId: String, transaction: ScheduledTransaction) {
        repository.add(walletId, transaction)
    }

    override suspend fun remove(walletId: String) {
        repository.remove(walletId)
    }

    override suspend fun setAt(walletId: String, transaction: ScheduledTransaction, index: Long) {
        repository.setAtPosition(walletId, transaction, index)
    }

    override suspend fun hasTransactions(walletId: String): Boolean {
        val count = repository.count(walletId)
                .awaitFirstOrDefault(0)

        return count > 0
    }

    override suspend fun first(walletId: String): ScheduledTransaction {
        val transaction = repository.first(walletId)
                .awaitFirstOrNull() ?: throw NotFoundException("Transaction not found")

        return transaction as ScheduledTransaction
    }

    override suspend fun findAll(walletId: String): List<ScheduledTransaction> {
        val count = repository.count(walletId)
                .awaitFirstOrDefault(0)

        return repository.findAll(walletId, 0, count)
                .map {
                    it as ScheduledTransaction
                }
                .collectList()
                .awaitFirstOrDefault(emptyList())
    }
}
