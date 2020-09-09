package io.openfuture.state.repository

import com.mongodb.internal.connection.tlschannel.util.Util.assertTrue
import io.openfuture.state.base.RedisRepositoryTests
import io.openfuture.state.property.WatcherProperties
import io.openfuture.state.util.createDummyBlockchain
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.isMemberAndAwait
import org.springframework.data.redis.core.setAndAwait
import java.time.LocalDateTime

class ProcessingRedisRepositoryTest : RedisRepositoryTests() {

    private lateinit var processingRedisRepository: ProcessingRedisRepository
    private val watcherProperties: WatcherProperties = WatcherProperties(1000, 1000, WatcherProperties.Lock(10))

    @BeforeEach
    fun setUp() {
        processingRedisRepository = ProcessingRedisRepository(redisTemplate, watcherProperties)
        redisTemplate.execute {
            it.serverCommands().flushAll()
        }.blockFirst()
    }

    @Test
    fun getCurrentShouldReturnZero() = runBlocking<Unit> {
        val result = processingRedisRepository.getCurrent(createDummyBlockchain())
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun getCurrentShouldReturnProperValue() = runBlocking<Unit> {
        val blockchain = createDummyBlockchain()
        redisTemplate.opsForValue().setAndAwait("$blockchain:current", 11)

        val result = processingRedisRepository.getCurrent(blockchain)
        assertThat(result).isEqualTo(11)
    }

    @Test
    fun getLastReturnZero() = runBlocking<Unit> {
        val result = processingRedisRepository.getLast(createDummyBlockchain())
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun getLastShouldReturnProperValue() = runBlocking<Unit> {
        val blockchain = createDummyBlockchain()
        redisTemplate.opsForValue().setAndAwait("$blockchain:last", 11)
        val result = processingRedisRepository.getLast(blockchain)
        assertThat(result).isEqualTo(11)
    }

    @Test
    fun setLastShouldUpdateTtl() = runBlocking<Unit> {
        val blockchain = createDummyBlockchain()
        processingRedisRepository.setLast(blockchain, 55)

        val result = processingRedisRepository.getLast(blockchain)

        assertThat(result).isEqualTo(55)
    }

    @Test
    fun incCurrentShouldReturnSequence() = runBlocking<Unit> {
        val blockchain = createDummyBlockchain()
        assertThat(processingRedisRepository.incCurrent(blockchain)).isEqualTo(1)
        assertThat(processingRedisRepository.incCurrent(blockchain)).isEqualTo(2)
        assertThat(processingRedisRepository.incCurrent(blockchain)).isEqualTo(3)
    }

    @Test
    fun lockIfAbsentReturnTrue() = runBlocking {
        val blockchain = createDummyBlockchain()
        val result = processingRedisRepository.lockIfAbsent(blockchain)
        assertTrue(result)
    }

    @Test
    fun lockIfAbsentReturnFalse() = runBlocking {
        val blockchain = createDummyBlockchain()
        redisTemplate.opsForValue().setAndAwait("lock:${blockchain}", LocalDateTime.now(), watcherProperties.lock!!.ttl)
        val result = processingRedisRepository.lockIfAbsent(blockchain)
        assertFalse(result)
    }

    @Test
    fun queueShouldAdd() = runBlocking {
        val blockchain = createDummyBlockchain()

        processingRedisRepository.queue(blockchain)

        val result = redisTemplate.opsForSet().isMemberAndAwait("queue", blockchain.getName())

        assertTrue(result)
    }

    @Test
    fun popShouldReturnNull() = runBlocking {
        val result = processingRedisRepository.pop()

        assertThat(result).isNull()
    }

    @Test
    fun popShouldReturnBlockchainName() = runBlocking<Unit> {
        val blockchain = createDummyBlockchain()

        processingRedisRepository.queue(blockchain)

        val result = processingRedisRepository.pop()

        assertThat(result).isEqualTo(blockchain.getName())
    }
}
