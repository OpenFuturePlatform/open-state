package io.openfuture.state.repository

import io.openfuture.state.base.RedisRepositoryTests
import io.openfuture.state.property.WebhookProperties
import io.openfuture.state.util.createDummyTransactionQueueTask
import io.openfuture.state.util.toEpochMilli
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.hasKeyAndAwait
import org.springframework.data.redis.core.setAndAwait
import java.time.Duration
import java.time.LocalDateTime

internal class WebhookQueueRedisRepositoryTest : RedisRepositoryTests() {

    private lateinit var repository: WebhookQueueRedisRepository
    private val properties: WebhookProperties = WebhookProperties(lockTTL = Duration.ofSeconds(3))


    @BeforeEach
    fun setUp() {
        repository = WebhookQueueRedisRepository(commonRedisTemplate, transactionTaskRedisTemplate, properties)

        commonRedisTemplate.execute { it.serverCommands().flushAll() }.blockFirst()
        transactionTaskRedisTemplate.execute { it.serverCommands().flushAll() }.blockFirst()
    }

    @Test
    fun addTransactionShouldAddElement() = runBlocking<Unit> {
        val transactionTask = createDummyTransactionQueueTask()
        repository.addTransaction("walletId", transactionTask)

        val result = transactionTaskRedisTemplate.opsForList()
            .range("walletId", 0, 1)
            .collectList().block()

        assertThat(result).isEqualTo(listOf(transactionTask))
    }

    @Test
    fun addTransactionShouldAddElementToTheEndOfQueue() = runBlocking<Unit> {
        val transactionTask1 = createDummyTransactionQueueTask("1")
        val transactionTask2 = createDummyTransactionQueueTask("2")

        repository.addTransaction("walletId", transactionTask1)
        repository.addTransaction("walletId", transactionTask2)

        val result = transactionTaskRedisTemplate.opsForList()
            .range("walletId", 0, 1)
            .collectList()
            .block()

        assertThat(result).isEqualTo(listOf(transactionTask1, transactionTask2))
    }

    @Test
    fun addWalletShouldAddItemToZSet() = runBlocking<Unit> {
        val transactionTask = createDummyTransactionQueueTask()

        repository.addWallet("walletId", transactionTask, 15.0)
        val result = commonRedisTemplate.opsForZSet().score(WALLETS_QUEUE, "walletId").block()

        assertThat(result).isEqualTo(15.0)
    }

    @Test
    fun addWalletShouldAddTransactionToList() = runBlocking<Unit> {
        val transactionTask = createDummyTransactionQueueTask()

        repository.addWallet("walletId", transactionTask, 15.0)
        val result = transactionTaskRedisTemplate.opsForList()
            .range("walletId", 0, 1)
            .collectList().block()

        assertThat(result).isEqualTo(listOf(transactionTask))
    }

    @Test
    fun walletScoreShouldReturnNull() = runBlocking {
        val result = repository.walletScore("walletId")
        assertThat(result).isNull()
    }

    @Test
    fun walletScoreShouldReturnProperValue() = runBlocking<Unit> {
        commonRedisTemplate.opsForZSet().add(WALLETS_QUEUE, "walletId", 1.0).block()

        val result = repository.walletScore("walletId")
        assertThat(result).isEqualTo(1.0)
    }

    @Test
    fun firstWalletInScoreRangeShouldReturnNullIfRangeStartNotSet() = runBlocking {
        val result = repository.firstWalletInScoreRange(null, LocalDateTime.now().toEpochMilli().toDouble())
        assertThat(result).isNull()
    }

    @Test
    fun firstWalletInScoreRangeShouldReturnNullIfRangeStartIsSet() = runBlocking {
        val result = repository.firstWalletInScoreRange(1000.0, LocalDateTime.now().toEpochMilli().toDouble())
        assertThat(result).isNull()
    }

    @Test
    fun firstWalletInScoreRangeShouldReturnProperValuesIfStartOfRangeNotSet() = runBlocking<Unit> {
        val timestamp = LocalDateTime.now()

        commonRedisTemplate.opsForZSet()
            .add(WALLETS_QUEUE, "walletId1", timestamp.minusDays(1).toEpochMilli().toDouble()).block()
        commonRedisTemplate.opsForZSet().add(WALLETS_QUEUE, "walletId2", timestamp.toEpochMilli().toDouble()).block()
        commonRedisTemplate.opsForZSet()
            .add(WALLETS_QUEUE, "walletId3", timestamp.plusDays(1).toEpochMilli().toDouble()).block()

        val result = repository.firstWalletInScoreRange(null, timestamp.toEpochMilli().toDouble())
        assertThat(result).isEqualTo("walletId1")
    }

    @Test
    fun firstWalletInScoreRangeShouldReturnProperValuesIfStartOfRangeIsSet() = runBlocking<Unit> {
        val timestamp = LocalDateTime.now()

        commonRedisTemplate.opsForZSet()
            .add(WALLETS_QUEUE, "walletId1", timestamp.minusDays(1).toEpochMilli().toDouble()).block()
        commonRedisTemplate.opsForZSet().add(WALLETS_QUEUE, "walletId2", timestamp.toEpochMilli().toDouble()).block()
        commonRedisTemplate.opsForZSet()
            .add(WALLETS_QUEUE, "walletId3", timestamp.plusDays(1).toEpochMilli().toDouble()).block()

        val result = repository.firstWalletInScoreRange(
            timestamp.toEpochMilli().toDouble(),
            timestamp.plusDays(3).toEpochMilli().toDouble()
        )
        assertThat(result).isEqualTo("walletId2")
    }

    @Test
    fun firstWalletInScoreRangeShouldReturnNull() = runBlocking<Unit> {
        val timestamp = LocalDateTime.now()

        commonRedisTemplate.opsForZSet()
            .add(WALLETS_QUEUE, "walletId1", timestamp.minusDays(1).toEpochMilli().toDouble()).block()
        commonRedisTemplate.opsForZSet().add(WALLETS_QUEUE, "walletId2", timestamp.toEpochMilli().toDouble()).block()
        commonRedisTemplate.opsForZSet()
            .add(WALLETS_QUEUE, "walletId3", timestamp.plusDays(1).toEpochMilli().toDouble()).block()

        val result = repository.firstWalletInScoreRange(
            timestamp.plusDays(2).toEpochMilli().toDouble(),
            timestamp.plusDays(3).toEpochMilli().toDouble()
        )
        assertThat(result).isNull()
    }

    @Test
    fun lockShouldReturnReceiveLock() = runBlocking<Unit> {
        val result = repository.lock("walletId")
        assertThat(result).isTrue
    }

    @Test
    fun lockShouldReturnNotReceiveLock() = runBlocking<Unit> {
        commonRedisTemplate.opsForValue().setAndAwait("LOCK:walletId", "walletId", properties.lockTTL)

        val result = repository.lock("walletId")
        assertThat(result).isFalse
    }

    @Test
    fun lockShouldExpire(): Unit = runBlocking {
        repository.lock("walletId")
        val exists = commonRedisTemplate.hasKeyAndAwait("LOCK:walletId")
        assertThat(exists).isTrue

        delay(properties.lockTTL.toMillis())

        val existsAfterTtl = commonRedisTemplate.hasKeyAndAwait("lock:walletId")
        assertThat(existsAfterTtl).isFalse
    }

    @Test
    fun firstTransactionShouldReturnNull() = runBlocking<Unit> {
        val result = repository.firstTransaction("walletId")
        assertThat(result).isNull()
    }

    @Test
    fun firstTransactionShouldReturnProperValue() = runBlocking<Unit> {
        val transactionTask = createDummyTransactionQueueTask()
        transactionTaskRedisTemplate.opsForList().rightPush("walletId", transactionTask).block()

        val result = repository.firstTransaction("walletId")
        assertThat(result).isEqualTo(transactionTask)
    }

    @Test
    fun firstTransactionShouldReturnFirstValueInList() = runBlocking<Unit> {
        val transactionTask1 = createDummyTransactionQueueTask("transactionId1")
        val transactionTask2 = createDummyTransactionQueueTask("transactionId2")
        val transactionTask3 = createDummyTransactionQueueTask("transactionId3")

        transactionTaskRedisTemplate.opsForList().rightPush("walletId", transactionTask1).block()
        transactionTaskRedisTemplate.opsForList().rightPush("walletId", transactionTask2).block()
        transactionTaskRedisTemplate.opsForList().rightPush("walletId", transactionTask3).block()

        val result = repository.firstTransaction("walletId")

        assertThat(result).isEqualTo(transactionTask1)
    }

    companion object {
        private const val WALLETS_QUEUE = "wallets_queue"
    }

}
