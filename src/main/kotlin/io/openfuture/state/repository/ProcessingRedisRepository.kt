package io.openfuture.state.repository

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.property.LockProperties
import org.springframework.data.redis.core.*
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.LocalDateTime
import javax.annotation.PostConstruct

@Repository
class ProcessingRedisRepository(
        reactiveRedisTemplate: ReactiveRedisTemplate<String, Any>,
        private val lockProperties: LockProperties
) {
    private val setOperations: ReactiveSetOperations<String, Any> = reactiveRedisTemplate.opsForSet()
    private val valueOperations: ReactiveValueOperations<String, Any> = reactiveRedisTemplate.opsForValue()

    suspend fun getCurrent(blockchain: Blockchain): Long {
        return (valueOperations.getAndAwait("$blockchain:$CURRENT") as Int?)?.toLong() ?: 0
    }

    suspend fun getLast(blockchain: Blockchain): Long {
        return (valueOperations.getAndAwait("$blockchain:$LAST") as Int?)?.toLong() ?: 0
    }

    suspend fun incCurrent(blockchain: Blockchain): Long {
        return valueOperations.incrementAndAwait("$blockchain:$CURRENT")
    }

    suspend fun setLast(blockchain: Blockchain, value: Long): Boolean {
        return valueOperations.setAndAwait("$blockchain:$LAST", value)
    }

    suspend fun lock(blockchain: Blockchain): Boolean {
        return valueOperations.setAndAwait(
                "$LOCK:$blockchain",
                LocalDateTime.now(),
                Duration.ofSeconds(lockProperties.ttl)
        )
    }

    suspend fun unlock(blockchain: Blockchain): Boolean {
        return valueOperations.deleteAndAwait("$LOCK:$blockchain")
    }

    suspend fun lockIfAbsent(blockchain: Blockchain): Boolean {
        return valueOperations.setIfAbsentAndAwait(
                "$LOCK:$blockchain",
                LocalDateTime.now(),
                Duration.ofSeconds(lockProperties.ttl)
        )
    }

    suspend fun queue(blockchain: Blockchain) {
        setOperations.addAndAwait(QUEUE, blockchain.getName())
    }

    suspend fun pop(): String? {
        return setOperations.popAndAwait(QUEUE) as String?
    }

    companion object {
        private const val CURRENT = "current"
        private const val LAST = "last"
        private const val LOCK = "lock"
        private const val QUEUE = "queue"
    }
}
