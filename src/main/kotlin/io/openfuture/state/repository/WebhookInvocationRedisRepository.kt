package io.openfuture.state.repository

import io.openfuture.state.domain.WebhookInvocation
import io.openfuture.state.property.WebhookProperties
import org.springframework.data.redis.core.*
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.time.LocalDateTime

@Repository
class WebhookInvocationRedisRepository(
        private val redisTemplate: ReactiveRedisTemplate<String, Any>,
        private val webhookProperties: WebhookProperties
) {

    private val webhooks: ReactiveZSetOperations<String, Any> = redisTemplate.opsForZSet()
    private val locks: ReactiveValueOperations<String, Any> = redisTemplate.opsForValue()

    suspend fun add(webhookInvocation: WebhookInvocation) {
        webhooks.addAndAwait(WEBHOOKS_QUEUE,
                webhookInvocation,
                webhookInvocation.score()
        )
    }

    suspend fun remove(webhookInvocation: WebhookInvocation) {
        webhooks.remove(WEBHOOKS_QUEUE, webhookInvocation)
    }

    suspend fun webhooksQueue(): Flux<ZSetOperations.TypedTuple<Any>> {
       return webhooks.scan(WEBHOOKS_QUEUE)
    }

       suspend fun lock(webhookInvocation: WebhookInvocation): Boolean {
       return locks.setIfAbsentAndAwait(
                "$LOCK:${webhookInvocation.address()}",
                LocalDateTime.now(),
                webhookProperties.lockTtl
        )
    }

    suspend fun unlock(webhookInvocation: WebhookInvocation) {
        locks.deleteAndAwait("$LOCK:${webhookInvocation.address()}")
    }

    companion object {
        private const val LOCK = "lock"
        private const val WEBHOOKS_QUEUE= "webhooks_queue"
    }

}
