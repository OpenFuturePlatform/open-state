package io.openfuture.state.repository

import com.mongodb.internal.connection.tlschannel.util.Util.assertTrue
import io.openfuture.state.base.RedisRepositoryTests
import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.property.WatcherProperties
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.springframework.data.redis.core.isMemberAndAwait
import org.springframework.data.redis.core.setAndAwait
import java.time.LocalDateTime

class ProcessingRedisRepositoryTest : RedisRepositoryTests() {

    private lateinit var processingRedisRepository: ProcessingRedisRepository
    private val watcherProperties: WatcherProperties = WatcherProperties()

    @Mock
    private lateinit var blockchain: Blockchain

    @BeforeEach
    fun setUp() {
        processingRedisRepository = ProcessingRedisRepository(redisTemplate, watcherProperties)
        redisTemplate.execute {
            it.serverCommands().flushAll()
        }.blockFirst()
    }

    @Test
    fun getCurrentShouldReturnZero() = runBlocking<Unit> {
        val result = processingRedisRepository.getCurrent(blockchain)
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun getCurrentShouldReturnProperValue() = runBlocking<Unit> {
        redisTemplate.opsForValue().setAndAwait("$blockchain:current", 11)

        val result = processingRedisRepository.getCurrent(blockchain)
        assertThat(result).isEqualTo(11)
    }

    @Test
    fun getLastReturnZero() = runBlocking<Unit> {
        val result = processingRedisRepository.getLast(blockchain)
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun getLastShouldReturnProperValue() = runBlocking<Unit> {
        redisTemplate.opsForValue().setAndAwait("$blockchain:last", 11)
        val result = processingRedisRepository.getLast(blockchain)
        assertThat(result).isEqualTo(11)
    }

    @Test
    fun setLastShouldUpdateTtl() = runBlocking<Unit> {
        processingRedisRepository.setLast(blockchain, 55)

        val result = processingRedisRepository.getLast(blockchain)

        assertThat(result).isEqualTo(55)
    }

    @Test
    fun incCurrentShouldReturnSequence() = runBlocking<Unit> {
        assertThat(processingRedisRepository.incCurrent(blockchain)).isEqualTo(1)
        assertThat(processingRedisRepository.incCurrent(blockchain)).isEqualTo(2)
        assertThat(processingRedisRepository.incCurrent(blockchain)).isEqualTo(3)
    }

    @Test
    fun lockIfAbsentReturnTrue() = runBlocking {
        val result = processingRedisRepository.lockIfAbsent(blockchain)
        assertTrue(result)
    }

    @Test
    fun lockIfAbsentReturnFalse() = runBlocking {
        redisTemplate.opsForValue().setAndAwait("lock:${blockchain}", LocalDateTime.now(), watcherProperties.lock!!.ttl!!)
        val result = processingRedisRepository.lockIfAbsent(blockchain)
        assertFalse(result)
    }

    @Test
    fun queueShouldAdd() = runBlocking {
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
        processingRedisRepository.queue(blockchain)

        val result = processingRedisRepository.pop()

        assertThat(result).isEqualTo(blockchain.getName())
    }
}
