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

    override suspend fun addTransaction(walletAddress: String, transaction: ScheduledTransaction) {
        repository.add(walletAddress, jsonSerializer.toJson(transaction))
    }

    override suspend fun removeTransactions(walletAddress: String) {
        repository.remove(walletAddress)
    }

    override suspend fun setAt(walletAddress: String, transaction: ScheduledTransaction, index: Long) {
        repository.setAtPosition(walletAddress, jsonSerializer.toJson(transaction), index)
    }

    override suspend fun hasTransactions(walletAddress: String): Boolean {
        val count = repository.count(walletAddress)
                .awaitFirstOrDefault(0)

        return count > 0
    }

    override suspend fun firstTransaction(walletAddress: String): ScheduledTransaction {
        val transaction = repository.first(walletAddress)
                .awaitFirstOrNull() ?: throw NotFoundException("Transaction not found")

        val value = transaction as String
        return jsonSerializer.fromJson(value, ScheduledTransaction::class.java)
    }

    override suspend fun findAll(walletAddress: String): List<ScheduledTransaction> {
        val count = repository.count(walletAddress)
                .awaitFirstOrDefault(0)

        return repository.findAll(walletAddress, 0, count)
                .map {
                    jsonSerializer.fromJson(it as String, ScheduledTransaction::class.java)
                }
                .collectList()
                .awaitFirstOrDefault(emptyList())
    }
}
