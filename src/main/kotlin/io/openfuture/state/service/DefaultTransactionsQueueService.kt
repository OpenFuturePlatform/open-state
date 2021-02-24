package io.openfuture.state.service

import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.TransactionsRedisRepository
import io.openfuture.state.util.JsonUtil
import io.openfuture.state.webhook.ScheduledTransaction
import kotlinx.coroutines.reactive.awaitFirstOrDefault
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service

@Service
class DefaultTransactionsQueueService(
        private val repository: TransactionsRedisRepository
): TransactionsQueueService {

    override suspend fun addTransaction(walletAddress: String, transaction: ScheduledTransaction) {
        repository.add(walletAddress, JsonUtil.toJson(transaction))
    }

    override suspend fun removeTransactions(walletAddress: String) {
        repository.remove(walletAddress)
    }

    override suspend fun setAt(walletAddress: String, transaction: ScheduledTransaction, index: Long) {
        repository.setAtPosition(walletAddress, JsonUtil.toJson(transaction), index)
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
        return JsonUtil.fromJson(value, ScheduledTransaction::class.java)
    }

    override suspend fun findAll(walletAddress: String): List<ScheduledTransaction> {
        val count = repository.count(walletAddress)
                .awaitFirstOrDefault(0)

        return repository.findAll(walletAddress, 0, count)
                .map {
                    JsonUtil.fromJson(it as String, ScheduledTransaction::class.java)
                }
                .collectList()
                .awaitFirstOrDefault(emptyList())
    }
}
