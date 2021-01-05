package io.openfuture.state.repository

import io.openfuture.state.base.RedisRepositoryTests
import io.openfuture.state.property.WebhookProperties
import io.openfuture.state.util.createDummyWallet
import io.openfuture.state.util.createDummyWebhookInvocation
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.hasKeyAndAwait
import java.time.Duration

internal class WebhookInvocationRedisRepositoryTest : RedisRepositoryTests() {

    private val properties: WebhookProperties = WebhookProperties(lockTtl = Duration.ofSeconds(3))

    private lateinit var repository: WebhookInvocationRedisRepository

    @BeforeEach
    fun setUp() {
        repository = WebhookInvocationRedisRepository(redisTemplate, properties)
        redisTemplate.execute {
            it.serverCommands().flushAll()
        }.blockFirst()
    }

    @Test
    fun lockShouldExpire(): Unit = runBlocking {
        val webhook = createDummyWebhookInvocation()
        repository.lock(webhook)
        val exists = redisTemplate.hasKeyAndAwait(webhook.address())
        Assertions.assertThat(exists).isTrue

        delay(properties.lockTtl.toMillis())

        val existsAfterTtl = redisTemplate.hasKeyAndAwait(webhook.address())
        Assertions.assertThat(existsAfterTtl).isFalse()
    }

    @Test
    fun addShouldSuccess() = runBlocking<Unit> {
        repository.add(createDummyWebhookInvocation())
    }
}
