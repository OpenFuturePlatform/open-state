package io.openfuture.state.repository

import io.openfuture.state.domain.TransactionQueueTask
import org.springframework.data.redis.core.ReactiveListOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.rightPushAndAwait
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class TransactionQueueRedisRepository(
        private val redisTemplate: ReactiveRedisTemplate<String, TransactionQueueTask>
) {

    private val transactions: ReactiveListOperations<String, TransactionQueueTask> = redisTemplate.opsForList()


    suspend fun add(walletId: String, transaction: TransactionQueueTask) {
        transactions.rightPushAndAwait(walletId, transaction)
    }

    suspend fun first(walletId: String): Mono<TransactionQueueTask> {
        return transactions.leftPop(walletId)
    }
}
