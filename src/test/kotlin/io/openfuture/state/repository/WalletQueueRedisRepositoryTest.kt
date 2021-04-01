package io.openfuture.state.repository

import com.fasterxml.jackson.databind.ObjectMapper
import io.openfuture.state.base.RedisRepositoryTests
import io.openfuture.state.domain.TransactionQueueTask
import io.openfuture.state.util.createDummyTransactionQueueTask
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class WalletQueueRedisRepositoryTest: RedisRepositoryTests() {

    private lateinit var repository: WalletQueueRedisRepository


    @BeforeEach
    fun setUp() {
        repository = WalletQueueRedisRepository(redisTemplate)
        redisTemplate.execute {
            it.serverCommands().flushAll()
        }.blockFirst()
    }

    @Test
    fun addShouldRAddItemToZSet() = runBlocking<Unit> {
        val transactionTask = createDummyTransactionQueueTask()

        repository.add("walletId", transactionTask, 15.0)
        val result = redisTemplate.opsForZSet().score(WALLETS_QUEUE, "walletId").block()

        Assertions.assertThat(result).isEqualTo(15.0)
    }

    @Test
    fun addShouldRAddItemToList() = runBlocking<Unit> {
        val transactionTask = createDummyTransactionQueueTask()

        repository.add("walletId", transactionTask, 15.0)
        val result = redisTemplate.opsForList()
                .range("walletId", 0, 1)
                .map { ObjectMapper().convertValue(it, TransactionQueueTask::class.java) }
                .collectList().block()

        Assertions.assertThat(result).isEqualTo(listOf(transactionTask))
    }

    @Test
    fun scoreShouldReturnNull() = runBlocking {
        val result = repository.score("walletId").block()
        Assertions.assertThat(result).isNull()
    }

    @Test
    fun scoreShouldReturnProperValue() = runBlocking<Unit> {
        redisTemplate.opsForZSet().add(WALLETS_QUEUE, "walletId", 1.0).block()

        val result = repository.score("walletId").block()
        Assertions.assertThat(result).isEqualTo(1.0)
    }


    companion object {
        private const val WALLETS_QUEUE = "wallets_queue"
    }
}
