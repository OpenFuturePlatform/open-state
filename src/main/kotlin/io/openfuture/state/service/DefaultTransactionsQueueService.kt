package io.openfuture.state.service

import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.TransactionsRedisRepository
import io.openfuture.state.util.JsonSerializer
import io.openfuture.state.webhook.ScheduledTransaction
import kotlinx.coroutines.reactive.awaitFirstOrDefault
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Service

@Service
class DefaultTransactionsQueueService(
        private val repository: TransactionsRedisRepository,
        private val jsonSerializer: JsonSerializer
): TransactionsQueueService {

    override suspend fun add(walletKey: String, transaction: ScheduledTransaction) {
        repository.add(walletKey, jsonSerializer.toJson(transaction))
    }

    override suspend fun remove(walletKey: String) {
        repository.remove(walletKey)
    }

    override suspend fun setAt(walletKey: String, transaction: ScheduledTransaction, index: Long) {
        repository.setAtPosition(walletKey, jsonSerializer.toJson(transaction), index)
    }

    override suspend fun hasTransactions(walletKey: String): Boolean {
        val count = repository.count(walletKey)
                .awaitFirstOrDefault(0)

        return count > 0
    }

    override suspend fun first(walletKey: String): ScheduledTransaction {
        val transaction = repository.first(walletKey)
                .awaitFirstOrNull() ?: throw NotFoundException("Transaction not found")

        val value = transaction as String
        return jsonSerializer.fromJson(value, ScheduledTransaction::class.java)
    }

    override suspend fun findAll(walletKey: String): List<ScheduledTransaction> {
        val count = repository.count(walletKey)
                .awaitFirstOrDefault(0)

        return repository.findAll(walletKey, 0, count)
                .map {
                    jsonSerializer.fromJson(it as String, ScheduledTransaction::class.java)
                }
                .collectList()
                .awaitFirstOrDefault(emptyList())
    }
}
