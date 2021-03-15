package io.openfuture.state.repository

import io.openfuture.state.extension.serialize
import io.openfuture.state.property.WebhookProperties
import io.openfuture.state.util.toEpochMilli
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


    suspend fun add(walletKey: String, transaction: String, timestamp: LocalDateTime) {
        val result = redisTemplate.execute { connection ->
            val walletAdd = connection.zSetCommands().zAdd(
                    redisTemplate.serialize(WALLETS_QUEUE),
                    timestamp.toEpochMilli().toDouble(),
                    redisTemplate.serialize(walletKey)
            )

            val transactionAdd = connection.listCommands().rPush(
                    redisTemplate.serialize(walletKey),
                    listOf(redisTemplate.serialize(transaction))
            )

            Flux1.zip(walletAdd, transactionAdd)
        }

        result.awaitLast()
    }

    suspend fun walletScore(walletKey: String): Mono<Double> {
        return wallets.score(WALLETS_QUEUE, walletKey)
    }

    suspend fun incrementScore(walletKey: String, scoreDiff: Double) {
        wallets.incrementScoreAndAwait(
                WALLETS_QUEUE,
                walletKey,
                scoreDiff
        )
    }

    suspend fun remove(walletKey: String) {
       wallets.removeAndAwait(WALLETS_QUEUE, walletKey)
    }

    suspend fun lock(walletKey: String): Boolean {
        return locks.setIfAbsentAndAwait(
                "LOCK:${walletKey}",
                walletKey,
                webhookProperties.lockTtl
        )
    }

    suspend fun unlock(walletKey: String) {
        locks.deleteAndAwait("LOCK:${walletKey}")
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
