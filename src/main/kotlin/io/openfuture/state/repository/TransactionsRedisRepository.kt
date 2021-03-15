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


    suspend fun add(walletKey: String, transaction: String) {
        transactions.rightPushAndAwait(walletKey, transaction)
    }

    suspend fun setAtPosition(walletKey: String, transaction: String, index: Long) {
        transactions.setAndAwait(walletKey, index, transaction)
    }

    suspend fun first(walletKey: String) : Mono<Any> {
        return transactions.leftPop(walletKey)
    }

    suspend fun findAll(walletKey: String, start: Long, end: Long): Flux<Any> {
        return transactions.range(walletKey, start, end)
    }

    suspend fun count(walletKey: String) : Mono<Long> {
        return transactions.size(walletKey)
    }

    suspend fun remove(walletKey: String) {
        transactions.deleteAndAwait(walletKey)
    }
}
