package io.openfuture.state.repository

import io.openfuture.state.domain.TransactionQueueTask
import io.openfuture.state.extensions.keyToByteBuffer
import io.openfuture.state.extensions.toMillisDouble
import io.openfuture.state.extensions.valueToByteBuffer
import io.openfuture.state.property.WebhookProperties
import kotlinx.coroutines.reactive.awaitLast
import org.springframework.data.domain.Range
import org.springframework.data.redis.core.*
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Repository
class WalletQueueRedisRepository(
        private val redisTemplate: ReactiveRedisTemplate<String, Any>,
        private val webhookProperties: WebhookProperties
) {

    private val wallets: ReactiveZSetOperations<String, Any> = redisTemplate.opsForZSet()
    private val locks: ReactiveValueOperations<String, Any> = redisTemplate.opsForValue()


    suspend fun add(walletId: String, transaction: TransactionQueueTask, score: Double) {
        val result = redisTemplate.execute { connection ->
            val walletAdd = connection.zSetCommands().zAdd(
                    redisTemplate.keyToByteBuffer(WALLETS_QUEUE),
                    score,
                    redisTemplate.valueToByteBuffer(walletId)
            )

            val transactionAdd = connection.listCommands().rPush(
                    redisTemplate.keyToByteBuffer(walletId),
                    listOf(redisTemplate.valueToByteBuffer(transaction))
            )

            Flux.zip(walletAdd, transactionAdd)
        }

        result.awaitLast()
    }

    suspend fun score(walletId: String): Mono<Double> {
        return wallets.score(WALLETS_QUEUE, walletId)
    }

    suspend fun lock(walletId: String): Boolean {
        return locks.setIfAbsentAndAwait(
                "LOCK:${walletId}",
                LocalDateTime.now(),
                webhookProperties.lockTTL
        )
    }

    suspend fun walletsScheduledTo(toDate: LocalDateTime): Flux<String> {
        return wallets
                .rangeByScore(WALLETS_QUEUE, Range.closed(Double.MIN_VALUE, toDate.toMillisDouble()))
                .map { it as String }
    }

    suspend fun unlock(walletId: String) {
        locks.deleteAndAwait("LOCK:${walletId}")
    }

    companion object {
        private const val WALLETS_QUEUE = "wallets_queue"
    }
}
