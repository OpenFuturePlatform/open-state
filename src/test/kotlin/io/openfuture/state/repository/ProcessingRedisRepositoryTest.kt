package io.openfuture.state.repository

import com.mongodb.internal.connection.tlschannel.util.Util.assertTrue
import io.openfuture.state.base.RedisRepositoryTests
import io.openfuture.state.property.LockProperties
import io.openfuture.state.util.createDummyBlockchain
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.isMemberAndAwait
import java.time.LocalDateTime

class ProcessingRedisRepositoryTest : RedisRepositoryTests() {

    private lateinit var processingRedisRepository: ProcessingRedisRepository
    private val lockProperties: LockProperties = LockProperties(60)

    @BeforeEach
    fun setUp() {
        processingRedisRepository = ProcessingRedisRepository(reactiveRedisTemplate, lockProperties)
        reactiveRedisTemplate.execute {
            it.serverCommands().flushAll()
        }.blockFirst()
    }

    @Test
    fun getCurrentReturnZero() = runBlocking<Unit> {
        val result = processingRedisRepository.getCurrent(createDummyBlockchain())
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun getLastReturnZero() = runBlocking<Unit> {
        val result = processingRedisRepository.getLast(createDummyBlockchain())
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun setLastShouldSave() = runBlocking<Unit> {
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
        reactiveRedisTemplate.opsForValue().set("lock:${blockchain}", LocalDateTime.now())
        val result = processingRedisRepository.lockIfAbsent(blockchain)
        assertTrue(result)
    }

    @Test
    fun lockIfAbsentReturnFalse() = runBlocking {
        val blockchain = createDummyBlockchain()
        reactiveRedisTemplate.opsForValue().set("lock:${blockchain}", LocalDateTime.now())
        val result = processingRedisRepository.lockIfAbsent(blockchain)
        assertTrue(result)
    }

    @Test
    fun queueShouldAdd() = runBlocking<Unit> {
        val blockchain = createDummyBlockchain()

        processingRedisRepository.queue(blockchain)

        val result = reactiveRedisTemplate.opsForSet().isMemberAndAwait("queue",blockchain.getName())

        assertTrue(result)
    }

    @Test
    fun popShouldReturnNull() = runBlocking<Unit> {
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
