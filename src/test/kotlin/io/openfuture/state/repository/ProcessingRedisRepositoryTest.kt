package io.openfuture.state.repository

import com.mongodb.internal.connection.tlschannel.util.Util.assertTrue
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import io.openfuture.state.base.RedisRepositoryTests
import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.property.WatcherProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.hasKeyAndAwait
import org.springframework.data.redis.core.isMemberAndAwait
import org.springframework.data.redis.core.setAndAwait
import java.time.Duration
import java.time.LocalDateTime

class ProcessingRedisRepositoryTest : RedisRepositoryTests() {

    private val properties: WatcherProperties = WatcherProperties(lockTtl = Duration.ofSeconds(3))
    private val blockchain: Blockchain = mock()

    private lateinit var repository: ProcessingRedisRepository

    @BeforeEach
    fun setUp() {
        repository = ProcessingRedisRepository(redisTemplate, properties)
        redisTemplate.execute {
            it.serverCommands().flushAll()
        }.blockFirst()
        given(blockchain.getName()).willReturn("MockBlockchain")
        given(blockchain.toString()).willReturn(blockchain.getName())
    }

    @Test
    fun getCurrentShouldReturnZero() = runBlocking<Unit> {
        val result = repository.getCurrent(blockchain)
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun getCurrentShouldReturnProperValue() = runBlocking<Unit> {
        redisTemplate.opsForValue().setAndAwait("$blockchain:current", 11)

        val result = repository.getCurrent(blockchain)
        assertThat(result).isEqualTo(11)
    }

    @Test
    fun getLastReturnZero() = runBlocking<Unit> {
        val result = repository.getLast(blockchain)
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun getLastShouldReturnProperValue() = runBlocking<Unit> {
        redisTemplate.opsForValue().setAndAwait("$blockchain:last", 11)
        val result = repository.getLast(blockchain)
        assertThat(result).isEqualTo(11)
    }

    @Test
    fun setLastShouldUpdateTtl() = runBlocking<Unit> {
        repository.setLast(blockchain, 55)

        val result = repository.getLast(blockchain)

        assertThat(result).isEqualTo(55)
    }

    @Test
    fun incCurrentShouldReturnSequence() = runBlocking<Unit> {
        assertThat(repository.incCurrent(blockchain)).isEqualTo(1)
        assertThat(repository.incCurrent(blockchain)).isEqualTo(2)
        assertThat(repository.incCurrent(blockchain)).isEqualTo(3)
    }

    @Test
    fun lockIfAbsentReturnTrue() = runBlocking {
        val result = repository.lockIfAbsent(blockchain)
        assertTrue(result)
    }

    @Test
    fun lockIfAbsentReturnFalse(): Unit = runBlocking {
        redisTemplate.opsForValue().setAndAwait("lock:$blockchain", LocalDateTime.now(), properties.lockTtl)
        val result = repository.lockIfAbsent(blockchain)
        assertThat(result).isFalse()
    }

    @Test
    fun lockShouldExpire(): Unit = runBlocking {
        repository.lock(blockchain)
        val exists = redisTemplate.hasKeyAndAwait("lock:$blockchain")
        assertThat(exists).isTrue

        delay(properties.lockTtl.toMillis())

        val existsAfterTtl = redisTemplate.hasKeyAndAwait("lock:$blockchain")
        assertThat(existsAfterTtl).isFalse()
    }

    @Test
    fun queueShouldAdd() = runBlocking {
        repository.queue(blockchain)

        val result = redisTemplate.opsForSet().isMemberAndAwait("queue", blockchain.getName())

        assertTrue(result)
    }

    @Test
    fun popShouldReturnNull() = runBlocking {
        val result = repository.pop()

        assertThat(result).isNull()
    }

    @Test
    fun popShouldReturnBlockchainName() = runBlocking<Unit> {
        repository.queue(blockchain)

        val result = repository.pop()

        assertThat(result).isEqualTo(blockchain.getName())
    }
}
