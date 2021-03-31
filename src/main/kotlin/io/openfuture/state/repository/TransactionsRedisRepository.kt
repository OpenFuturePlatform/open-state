package io.openfuture.state.repository

import io.openfuture.state.webhook.ScheduledTransaction
import org.springframework.data.redis.core.*
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class TransactionsRedisRepository(
        private val redisTemplate: ReactiveRedisTemplate<String, Any>
) {

    private val transactions: ReactiveListOperations<String, Any> = redisTemplate.opsForList()


    suspend fun add(walletId: String, transaction: ScheduledTransaction) {
        transactions.rightPushAndAwait(walletId, transaction)
    }

    suspend fun setAtPosition(walletId: String, transaction: ScheduledTransaction, index: Long) {
        transactions.setAndAwait(walletId, index, transaction)
    }

    suspend fun first(walletId: String) : Mono<Any> {
        return transactions.leftPop(walletId)
    }

    suspend fun findAll(walletId: String, start: Long, end: Long): Flux<Any> {
        return transactions.range(walletId, start, end)
    }

    suspend fun count(walletId: String) : Mono<Long> {
        return transactions.size(walletId)
    }

    suspend fun remove(walletId: String) {
        transactions.deleteAndAwait(walletId)
    }
}
