package io.openfuture.state.repository

import io.openfuture.state.extension.serializeKey
import io.openfuture.state.extension.serializeValue
import io.openfuture.state.property.WebhookProperties
import io.openfuture.state.util.toEpochMilli
import io.openfuture.state.webhook.ScheduledTransaction
import kotlinx.coroutines.reactive.awaitLast
import org.springframework.data.domain.Range
import org.springframework.data.redis.core.*
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import reactor.core.publisher.Flux as Flux1

@Repository
class WalletWebhookRedisRepository(
        private val redisTemplate: ReactiveRedisTemplate<String, Any>,
        private val webhookProperties: WebhookProperties
) {

    private val wallets: ReactiveZSetOperations<String, Any> = redisTemplate.opsForZSet()
    private val locks: ReactiveValueOperations<String, Any> = redisTemplate.opsForValue()


    suspend fun add(walletId: String, transaction: ScheduledTransaction, timestamp: LocalDateTime) {
        val result = redisTemplate.execute { connection ->
            val walletAdd = connection.zSetCommands().zAdd(
                    redisTemplate.serializeKey(WALLETS_QUEUE),
                    timestamp.toEpochMilli().toDouble(),
                    redisTemplate.serializeValue(walletId)
            )

            val transactionAdd = connection.listCommands().rPush(
                    redisTemplate.serializeKey(walletId),
                    listOf(redisTemplate.serializeValue(transaction))
            )

            Flux1.zip(walletAdd, transactionAdd)
        }

        result.awaitLast()
    }

    suspend fun walletScore(walletId: String): Mono<Double> {
        return wallets.score(WALLETS_QUEUE, walletId)
    }

    suspend fun incrementScore(walletId: String, scoreDiff: Double) {
        wallets.incrementScoreAndAwait(
                WALLETS_QUEUE,
                walletId,
                scoreDiff
        )
    }

    suspend fun remove(walletId: String) {
       wallets.removeAndAwait(WALLETS_QUEUE, walletId)
    }

    suspend fun lock(walletId: String): Boolean {
        return locks.setIfAbsentAndAwait(
                "LOCK:${walletId}",
                walletId,
                webhookProperties.lockTtl
        )
    }

    suspend fun unlock(walletId: String) {
        locks.deleteAndAwait("LOCK:${walletId}")
    }

    suspend fun walletsScheduledTo(toDate: LocalDateTime): Flux1<String> {
        return wallets.rangeByScore(
                WALLETS_QUEUE,
                Range.closed(
                        Double.MIN_VALUE,
                        toDate.toEpochMilli().toDouble()
                )
        ).map { it as String }
    }

    companion object {
        private const val WALLETS_QUEUE= "wallets_queue"
    }
}
