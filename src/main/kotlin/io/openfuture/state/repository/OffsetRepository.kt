package io.openfuture.state.repository

import io.openfuture.state.model.BlockchainType
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class OffsetRepository(private val offsetRedisTemplate: ReactiveRedisTemplate<BlockchainType, Long>) {
    private val valueOperations: ReactiveValueOperations<BlockchainType, Long> = offsetRedisTemplate.opsForValue()

    fun getCurrent(blockchain: BlockchainType, defaultValueIfEmpty: Long = 0): Mono<Long> {
        return valueOperations[blockchain].defaultIfEmpty(defaultValueIfEmpty)
    }

    fun increment(blockchain: BlockchainType): Mono<Long> {
        return valueOperations.increment(blockchain)
    }
}
