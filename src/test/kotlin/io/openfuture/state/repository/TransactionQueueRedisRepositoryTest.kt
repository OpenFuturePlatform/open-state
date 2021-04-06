package io.openfuture.state.repository

import io.openfuture.state.config.RedisConfig
import io.openfuture.state.domain.TransactionQueueTask
import io.openfuture.state.util.createDummyTransactionQueueTask
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.ReactiveRedisTemplate

@DataRedisTest
@Import(RedisConfig::class, JacksonAutoConfiguration::class)
internal class TransactionQueueRedisRepositoryTest {

    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, TransactionQueueTask>
    private lateinit var repository: TransactionQueueRedisRepository


    @BeforeEach
    fun setUp() {
        repository = TransactionQueueRedisRepository(redisTemplate)
        redisTemplate.execute {
            it.serverCommands().flushAll()
        }.blockFirst()
    }

    @Test
    fun addShouldAddElement() = runBlocking<Unit> {
        val transactionTask = createDummyTransactionQueueTask()
        repository.add("walletId", transactionTask)

        val result = redisTemplate.opsForList()
                .range("walletId", 0, 1)
                .collectList()
                .block()
        Assertions.assertThat(result).isEqualTo(listOf(transactionTask))
    }

    @Test
    fun addShouldAddElementToEnd() = runBlocking<Unit> {
        val transactionTask1 = createDummyTransactionQueueTask("1")
        val transactionTask2 = createDummyTransactionQueueTask("2")

        repository.add("walletId", transactionTask1)
        repository.add("walletId", transactionTask2)

        val result = redisTemplate.opsForList().range("walletId", 0, 1).collectList().block()
        Assertions.assertThat(result).isEqualTo(listOf(transactionTask1, transactionTask2))
    }

    @Test
    fun firstShouldReturnNull() = runBlocking<Unit> {
        val result = repository.first("walletId").block()
        Assertions.assertThat(result).isNull()
    }

    @Test
    fun firstShouldReturnProperValue() = runBlocking<Unit> {
        val transactionTask = createDummyTransactionQueueTask()
        redisTemplate.opsForList().rightPush("walletId", transactionTask).block()

        val result = repository.first("walletId").block()
        Assertions.assertThat(result).isEqualTo(transactionTask)
    }

    @Test
    fun firstShouldReturnFirstValueInList() = runBlocking<Unit> {
        val transactionTask1 = createDummyTransactionQueueTask("transactionId1")
        val transactionTask2 = createDummyTransactionQueueTask("transactionId2")
        val transactionTask3 = createDummyTransactionQueueTask("transactionId3")

        redisTemplate.opsForList().rightPush("walletId", transactionTask1).block()
        redisTemplate.opsForList().rightPush("walletId", transactionTask2).block()
        redisTemplate.opsForList().rightPush("walletId", transactionTask3).block()

        val result = repository.first("walletId").block()

        Assertions.assertThat(result).isEqualTo(transactionTask1)
    }
}
