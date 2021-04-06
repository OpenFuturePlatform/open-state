package io.openfuture.state.repository

import io.openfuture.state.domain.TransactionQueueTask
import org.springframework.data.redis.core.*
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

    suspend fun count(walletId: String) : Mono<Long> {
        return transactions.size(walletId)
    }

    suspend fun remove(walletId: String) {
        transactions.deleteAndAwait(walletId)
    }

    suspend fun setAtPosition(walletId: String, transaction: TransactionQueueTask, index: Long) {
        transactions.setAndAwait(walletId, index, transaction)
    }
}
