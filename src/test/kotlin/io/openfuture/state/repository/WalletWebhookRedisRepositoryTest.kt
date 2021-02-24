package io.openfuture.state.repository

import com.mongodb.internal.connection.tlschannel.util.Util
import io.openfuture.state.base.RedisRepositoryTests
import io.openfuture.state.property.WebhookProperties
import io.openfuture.state.util.toEpochMilli
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

internal class WalletWebhookRedisRepositoryTest : RedisRepositoryTests() {

    private val properties: WebhookProperties = WebhookProperties(lockTtl = Duration.ofSeconds(3))
    private lateinit var repository: WalletWebhookRedisRepository


    @BeforeEach
    fun setUp() {
        repository = WalletWebhookRedisRepository(redisTemplate, properties)
        redisTemplate.execute {
            it.serverCommands().flushAll()
        }.blockFirst()
    }

    @Test
    fun walletScoreShouldReturnNull() = runBlocking<Unit> {
        val result = repository.walletScore("address").block()
        Assertions.assertThat(result).isNull()
    }

    @Test
    fun walletScoreShouldReturnProperValue() = runBlocking<Unit> {
        redisTemplate.opsForZSet().add(WALLETS_QUEUE, "address", 1.0).block()

        val result = repository.walletScore("address").block()
        Assertions.assertThat(result).isEqualTo(1.0)
    }

    @Test
    fun incrementScoreShouldChangeScoreValue() = runBlocking<Unit> {
        redisTemplate.opsForZSet().add(WALLETS_QUEUE, "address", 1.0).block()

        repository.incrementScore("address", 3.0)
        val result = redisTemplate.opsForZSet().score(WALLETS_QUEUE, "address").block()

        Assertions.assertThat(result).isEqualTo(4.0)
    }

    @Test
    fun removeShouldRemoveValueFromSet() = runBlocking<Unit> {
        redisTemplate.opsForZSet().add(WALLETS_QUEUE, "address", 1.0).block()

        repository.remove("address")
        val result = redisTemplate.opsForZSet().score(WALLETS_QUEUE, "address").block()

        Assertions.assertThat(result).isNull()
    }

    @Test
    fun addShouldRAddItemToZSet() = runBlocking<Unit> {
        val timestamp = LocalDateTime.now()

        repository.add("address", "transaction", timestamp)
        val result = redisTemplate.opsForZSet().score(WALLETS_QUEUE, "address").block()

        Assertions.assertThat(result).isEqualTo(timestamp.toEpochMilli().toDouble())
    }

    @Test
    fun addShouldRAddItemToList() = runBlocking<Unit> {
        val timestamp = LocalDateTime.now()

        repository.add("address", "transaction", timestamp)
        val result = redisTemplate.opsForList().range("address", 0, 1).collectList().block()

        Assertions.assertThat(result).isEqualTo(listOf("transaction"))
    }

    @Test
    fun walletsScheduledToShouldReturnEmptyList() = runBlocking<Unit> {
        val timestamp = LocalDateTime.now()

        redisTemplate.opsForZSet().add(WALLETS_QUEUE, "address1", timestamp.minusDays(1).toEpochMilli().toDouble()).block()
        redisTemplate.opsForZSet().add(WALLETS_QUEUE, "address2", timestamp.toEpochMilli().toDouble()).block()
        redisTemplate.opsForZSet().add(WALLETS_QUEUE, "address3", timestamp.plusDays(1) .toEpochMilli().toDouble()).block()

        val result = repository.walletsScheduledTo(timestamp.minusDays(2)).collectList().awaitSingle()

        Assertions.assertThat(result.size).isEqualTo(0)
    }

    @Test
    fun walletsScheduledToShouldReturnProperValues() = runBlocking<Unit> {
        val timestamp = LocalDateTime.now()

        redisTemplate.opsForZSet().add(WALLETS_QUEUE, "address1", timestamp.minusDays(1).toEpochMilli().toDouble()).block()
        redisTemplate.opsForZSet().add(WALLETS_QUEUE, "address2", timestamp.toEpochMilli().toDouble()).block()
        redisTemplate.opsForZSet().add(WALLETS_QUEUE, "address3", timestamp.plusDays(1) .toEpochMilli().toDouble()).block()

        val result = repository.walletsScheduledTo(timestamp).collectList().awaitSingle()
        Assertions.assertThat(result).isEqualTo(listOf("address1", "address2"))
    }

    @Test
    fun walletsScheduledToShouldReturnAllValues() = runBlocking<Unit> {
        val timestamp = LocalDateTime.now()

        redisTemplate.opsForZSet().add(WALLETS_QUEUE, "address1", timestamp.minusDays(1).toEpochMilli().toDouble()).block()
        redisTemplate.opsForZSet().add(WALLETS_QUEUE, "address2", timestamp.toEpochMilli().toDouble()).block()
        redisTemplate.opsForZSet().add(WALLETS_QUEUE, "address3", timestamp.plusDays(1).toEpochMilli().toDouble()).block()

        val result = repository.walletsScheduledTo(timestamp.plusDays(3)).collectList().awaitSingle()
        Assertions.assertThat(result).isEqualTo(listOf("address1", "address2", "address3"))
    }

    @Test
    fun lockReturnTrue() = runBlocking {
        val result = repository.lock("address")
        Util.assertTrue(result)
    }

    @Test
    fun lockReturnFalse(): Unit = runBlocking {
        redisTemplate.opsForValue().setAndAwait("LOCK:address", "address", properties.lockTtl)

        val result = repository.lock("address")
        Assertions.assertThat(result).isFalse()
    }

    @Test
    fun lockShouldExpire(): Unit = runBlocking {
        repository.lock("address")
        val exists = redisTemplate.hasKeyAndAwait("LOCK:address")
        Assertions.assertThat(exists).isTrue

        delay(properties.lockTtl.toMillis())

        val existsAfterTtl = redisTemplate.hasKeyAndAwait("lock:address")
        Assertions.assertThat(existsAfterTtl).isFalse()
    }

    companion object {
        private const val WALLETS_QUEUE = "wallets_queue"
    }
}
