package io.openfuture.state.repository

import com.fasterxml.jackson.databind.ObjectMapper
import io.openfuture.state.base.RedisRepositoryTests
import io.openfuture.state.domain.TransactionQueueTask
import io.openfuture.state.extensions.toMillisDouble
import io.openfuture.state.property.WebhookProperties
import io.openfuture.state.util.createDummyTransactionQueueTask
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.hasKeyAndAwait
import org.springframework.data.redis.core.setAndAwait
import java.time.Duration
import java.time.LocalDateTime

internal class WalletQueueRedisRepositoryTest: RedisRepositoryTests() {

    private lateinit var repository: WalletQueueRedisRepository
    private val properties: WebhookProperties = WebhookProperties(lockTTL = Duration.ofSeconds(3))


    @BeforeEach
    fun setUp() {
        repository = WalletQueueRedisRepository(redisTemplate, properties)
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

    @Test
    fun walletsScheduledToShouldReturnEmptyList() = runBlocking<Unit> {
        val timestamp = LocalDateTime.now()

        redisTemplate.opsForZSet().add(WALLETS_QUEUE, "walletId1", timestamp.minusDays(1).toMillisDouble()).block()
        redisTemplate.opsForZSet().add(WALLETS_QUEUE, "walletId2", timestamp.toMillisDouble()).block()
        redisTemplate.opsForZSet().add(WALLETS_QUEUE, "walletId3", timestamp.plusDays(1).toMillisDouble()).block()

        val result = repository.walletsScheduledTo(timestamp.minusDays(2)).collectList().awaitSingle()

        Assertions.assertThat(result.size).isEqualTo(0)
    }

    @Test
    fun walletsScheduledToShouldReturnProperValues() = runBlocking<Unit> {
        val timestamp = LocalDateTime.now()

        redisTemplate.opsForZSet().add(WALLETS_QUEUE, "walletId1", timestamp.minusDays(1).toMillisDouble()).block()
        redisTemplate.opsForZSet().add(WALLETS_QUEUE, "walletId2", timestamp.toMillisDouble()).block()
        redisTemplate.opsForZSet().add(WALLETS_QUEUE, "walletId3", timestamp.plusDays(1).toMillisDouble()).block()

        val result = repository.walletsScheduledTo(timestamp).collectList().block()
        Assertions.assertThat(result).isEqualTo(listOf("walletId1", "walletId2"))
    }

    @Test
    fun walletsScheduledToShouldReturnAllValues() = runBlocking<Unit> {
        val timestamp = LocalDateTime.now()

        redisTemplate.opsForZSet().add(WALLETS_QUEUE, "walletId1", timestamp.minusDays(1).toMillisDouble()).block()
        redisTemplate.opsForZSet().add(WALLETS_QUEUE, "walletId2", timestamp.toMillisDouble()).block()
        redisTemplate.opsForZSet().add(WALLETS_QUEUE, "walletId3", timestamp.plusDays(1).toMillisDouble()).block()

        val result = repository.walletsScheduledTo(timestamp.plusDays(3)).collectList().awaitSingle()
        Assertions.assertThat(result).isEqualTo(listOf("walletId1", "walletId2", "walletId3"))
    }

    @Test
    fun lockShouldReturnTrue() = runBlocking {
        val result = repository.lock("walletId")
        Assertions.assertThat(result).isTrue
    }

    @Test
    fun lockSholdReturnFalse(): Unit = runBlocking {
        redisTemplate.opsForValue().setAndAwait("LOCK:walletId", "walletId", properties.lockTTL)

        val result = repository.lock("walletId")
        Assertions.assertThat(result).isFalse()
    }

    @Test
    fun lockShouldExpire(): Unit = runBlocking {
        repository.lock("walletId")
        val exists = redisTemplate.hasKeyAndAwait("LOCK:walletId")
        Assertions.assertThat(exists).isTrue

        delay(properties.lockTTL.toMillis())

        val existsAfterTtl = redisTemplate.hasKeyAndAwait("lock:walletId")
        Assertions.assertThat(existsAfterTtl).isFalse()
    }

    companion object {
        private const val WALLETS_QUEUE = "wallets_queue"
    }
}
