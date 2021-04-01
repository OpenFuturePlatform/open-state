package io.openfuture.state.repository

import io.openfuture.state.domain.TransactionQueueTask
import io.openfuture.state.extensions.keyToByteBuffer
import io.openfuture.state.extensions.valueToByteBuffer
import kotlinx.coroutines.reactive.awaitLast
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveZSetOperations
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class WalletQueueRedisRepository(
        private val redisTemplate: ReactiveRedisTemplate<String, Any>
) {

    private val wallets: ReactiveZSetOperations<String, Any> = redisTemplate.opsForZSet()


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


    companion object {
        private const val WALLETS_QUEUE = "wallets_queue"
    }
}
