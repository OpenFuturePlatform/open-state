package io.openfuture.state.repository

import io.openfuture.state.model.Blockchain
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.LocalDateTime

@Repository
class BlockchainRepository(
        private val reactiveRedisTemplate: ReactiveRedisTemplate<String, Any>,
        @Value("\${watcher.lock-time-out}") private val lockTimeout: Long
) {
    private val valueOperations: ReactiveValueOperations<String, Any> = reactiveRedisTemplate.opsForValue()

    suspend fun getCurrentBlockNumber(blockchain: Blockchain): Long {
        return valueOperations["$CURRENT:$blockchain"].defaultIfEmpty(0)
                .map { (it as Int).toLong() }
                .awaitSingle()
    }

    suspend fun getLastBlockNumber(blockchain: Blockchain): Long {
        return valueOperations["$LAST:$blockchain"].defaultIfEmpty(0)
                .map { (it as Int).toLong() }
                .awaitSingle()
    }

    suspend fun incrementCurrentBlockNumber(blockchain: Blockchain): Long {
        return valueOperations.increment("$CURRENT:$blockchain").awaitSingle()
    }

    suspend fun setLastBlockNumber(blockchain: Blockchain, value: Long): Boolean {
        return valueOperations.set("$LAST:$blockchain", value).awaitSingle()
    }

    suspend fun lock(blockchain: Blockchain): Boolean {
        return valueOperations.setIfAbsent(
                "$LOCK:$blockchain",
                LocalDateTime.now(),
                Duration.ofSeconds(lockTimeout)
        ).awaitSingle()
    }

    suspend fun unlock(blockchain: Blockchain): Boolean {
        return valueOperations.delete("$LOCK:$blockchain").awaitSingle()
    }

    companion object {
        private const val CURRENT = "current"
        private const val LAST = "last"
        private const val LOCK = "lock"
    }
}
