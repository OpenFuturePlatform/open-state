package io.openfuture.state.repository

import io.openfuture.state.domain.TransactionQueueTask
import org.springframework.data.redis.core.ReactiveListOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.rightPushAndAwait
import org.springframework.stereotype.Repository

@Repository
class TransactionQueueRedisRepository(
        private val redisTemplate: ReactiveRedisTemplate<String, Any>
) {

    private val transactions: ReactiveListOperations<String, Any> = redisTemplate.opsForList()


    suspend fun add(walletId: String, transaction: TransactionQueueTask) {
        transactions.rightPushAndAwait(walletId, transaction)
    }
}
