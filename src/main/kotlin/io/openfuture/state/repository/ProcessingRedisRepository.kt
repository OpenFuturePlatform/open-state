package io.openfuture.state.repository

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.property.WatcherProperties
import org.springframework.data.redis.core.*
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ProcessingRedisRepository(
        redisTemplate: ReactiveRedisTemplate<String, Any>,
        private val watcherProperties: WatcherProperties
) {

    private val setOperations: ReactiveSetOperations<String, Any> = redisTemplate.opsForSet()
    private val valueOperations: ReactiveValueOperations<String, Any> = redisTemplate.opsForValue()

    suspend fun getCurrentOrElseLast(blockchain: Blockchain): Int {
        var current = valueOperations.getAndAwait("$blockchain:$CURRENT") as Int?
        if (null == current) {
            current = getLast(blockchain)
            valueOperations.setAndAwait("$blockchain:$CURRENT", current)
        }

        return current
    }

    suspend fun getLast(blockchain: Blockchain): Int {
        return valueOperations.getAndAwait("$blockchain:$LAST") as Int? ?: 0
    }

    suspend fun incCurrent(blockchain: Blockchain): Int {
        return valueOperations.incrementAndAwait("$blockchain:$CURRENT").toInt()
    }

    suspend fun setLast(blockchain: Blockchain, value: Int): Boolean {
        return valueOperations.setAndAwait("$blockchain:$LAST", value)
    }

    suspend fun lock(blockchain: Blockchain) {
        valueOperations.setAndAwait(
                "$LOCK:$blockchain",
                LocalDateTime.now(),
                watcherProperties.lockTtl
        )
    }

    suspend fun unlock(blockchain: Blockchain) {
        valueOperations.deleteAndAwait("$LOCK:$blockchain")
    }

    suspend fun lockIfAbsent(blockchain: Blockchain): Boolean {
        return valueOperations.setIfAbsentAndAwait(
                "$LOCK:$blockchain",
                LocalDateTime.now(),
                watcherProperties.lockTtl
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
