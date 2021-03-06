package io.openfuture.state.repository

import io.openfuture.state.domain.TransactionQueueTask
import io.openfuture.state.extensions.keyToByteBuffer
import io.openfuture.state.extensions.valueToByteBuffer
import io.openfuture.state.property.WebhookProperties
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitLast
import org.springframework.data.domain.Range
import org.springframework.data.redis.core.*
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.time.LocalDateTime

@Repository
class WebhookQueueRedisRepository(
    private val commonRedisTemplate: ReactiveRedisTemplate<String, Any>,
    private val webhookProperties: WebhookProperties,
    transactionTaskRedisTemplate: ReactiveRedisTemplate<String, TransactionQueueTask>,
) {
    private val wallets: ReactiveZSetOperations<String, Any> = commonRedisTemplate.opsForZSet()
    private val locks: ReactiveValueOperations<String, Any> = commonRedisTemplate.opsForValue()
    private val transactions: ReactiveListOperations<String, TransactionQueueTask> =
        transactionTaskRedisTemplate.opsForList()


    suspend fun addWallet(walletId: String, transactionTasks: Collection<TransactionQueueTask>, score: Double) {
        val result = commonRedisTemplate.execute { connection ->
            val walletAdd = connection.zSetCommands().zAdd(
                commonRedisTemplate.keyToByteBuffer(WALLETS_QUEUE),
                score,
                commonRedisTemplate.valueToByteBuffer(walletId)
            )

            val transactionAdd = connection.listCommands().rPush(
                commonRedisTemplate.keyToByteBuffer(walletId),
                transactionTasks.map {
                    commonRedisTemplate.valueToByteBuffer(it)
                }
            )

            Flux.zip(walletAdd, transactionAdd)
        }

        result.awaitLast()
    }

    suspend fun addTransactions(walletId: String, transactionTasks: Collection<TransactionQueueTask>) {
        transactions.rightPushAllAndAwait(walletId, transactionTasks)
    }

    suspend fun walletScore(walletId: String): Double? {
        return wallets.scoreAndAwait(WALLETS_QUEUE, walletId)
    }

    suspend fun lock(walletId: String): Boolean {
        return locks.setIfAbsentAndAwait(
            "LOCK:${walletId}",
            LocalDateTime.now(),
            webhookProperties.lockTTL
        )
    }

    suspend fun firstWalletInScoreRange(from: Double?, to: Double): String? {
        return wallets.rangeByScore(WALLETS_QUEUE, Range.closed(from ?: Double.MIN_VALUE, to))
            .map { it as String }
            .awaitFirstOrNull()
    }

    suspend fun unlock(walletId: String) {
        locks.deleteAndAwait("LOCK:${walletId}")
    }

    suspend fun firstTransaction(walletId: String): TransactionQueueTask? {
        return transactions.leftPopAndAwait(walletId)
    }

    suspend fun transactionsCount(walletId: String): Long {
        return transactions.sizeAndAwait(walletId)
    }

    suspend fun removeWalletFromQueue(walletId: String) {
        wallets.removeAndAwait(WALLETS_QUEUE, walletId)
        transactions.deleteAndAwait(walletId)
    }

    suspend fun setTransactionAtIndex(walletId: String, transaction: TransactionQueueTask, index: Long) {
        transactions.setAndAwait(walletId, index, transaction)
    }

    suspend fun changeScore(walletId: String, scoreDiff: Double) {
        wallets.incrementScoreAndAwait(WALLETS_QUEUE, walletId, scoreDiff)
    }

    companion object {
        private const val WALLETS_QUEUE = "wallets_queue"
    }

}
