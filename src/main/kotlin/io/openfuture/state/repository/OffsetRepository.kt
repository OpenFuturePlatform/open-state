package io.openfuture.state.repository

import io.openfuture.state.model.Blockchain
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class OffsetRepository(private val reactiveRedisTemplate: ReactiveRedisTemplate<String, Any>) {
    private val valueOperations: ReactiveValueOperations<String, Any> = reactiveRedisTemplate.opsForValue()

    fun getCurrent(blockchain: Blockchain): Mono<Long> {
        return valueOperations[blockchain.name].defaultIfEmpty(0).map { (it as Int).toLong() }
    }

    fun increment(blockchain: Blockchain): Mono<Long> {
        return valueOperations.increment(blockchain.name)
    }
}
