package io.openfuture.state.repository

import org.springframework.data.redis.core.*
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class TransactionsRedisRepository(
        private val redisTemplate: ReactiveRedisTemplate<String, Any>
) {

    private val transactions: ReactiveListOperations<String, Any> = redisTemplate.opsForList()


    suspend fun add(key: String, transaction: String) {
        transactions.rightPushAndAwait(key, transaction)
    }

    suspend fun setAtPosition(key: String, transaction: String, index: Long) {
        transactions.setAndAwait(key, index, transaction)
    }

    suspend fun first(key: String) : Mono<Any> {
        return transactions.leftPop(key)
    }

    suspend fun findAll(key: String, start: Long, end: Long): Flux<Any> {
        return transactions.range(key, start, end)
    }

    suspend fun count(key: String) : Mono<Long> {
        return transactions.size(key)
    }

    suspend fun remove(key: String) {
        transactions.deleteAndAwait(key)
    }
}
