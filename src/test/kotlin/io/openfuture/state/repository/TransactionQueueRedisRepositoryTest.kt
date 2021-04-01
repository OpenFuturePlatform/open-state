package io.openfuture.state.repository

import com.fasterxml.jackson.databind.ObjectMapper
import io.openfuture.state.base.RedisRepositoryTests
import io.openfuture.state.domain.TransactionQueueTask
import io.openfuture.state.util.createDummyTransactionQueueTask
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class TransactionQueueRedisRepositoryTest: RedisRepositoryTests() {

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
                .map { ObjectMapper().convertValue(it, TransactionQueueTask::class.java) }
                .collectList().block()

        Assertions.assertThat(result).isEqualTo(listOf(transactionTask))
    }

    @Test
    fun addShouldAddElementToEnd() = runBlocking<Unit> {
        val transactionTask1 = createDummyTransactionQueueTask("1")
        val transactionTask2 = createDummyTransactionQueueTask("2")

        repository.add("walletId", transactionTask1)
        repository.add("walletId", transactionTask2)

        val result = redisTemplate.opsForList()
                .range("walletId", 0, 1)
                .map { ObjectMapper().convertValue(it, TransactionQueueTask::class.java) }
                .collectList()
                .block()
        Assertions.assertThat(result).isEqualTo(listOf(transactionTask1, transactionTask2))
    }
}
