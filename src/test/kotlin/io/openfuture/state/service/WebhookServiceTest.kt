package io.openfuture.state.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import io.openfuture.state.domain.WebhookInvocation
import io.openfuture.state.repository.WebhookInvocationRedisRepository
import io.openfuture.state.repository.WebhookInvocationRepository
import io.openfuture.state.util.createDummyTransaction
import io.openfuture.state.util.createDummyWallet
import io.openfuture.state.util.createDummyWebhookInvocation
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono

internal class WebhookServiceTest {

    private lateinit var webhookService: WebhookService

    private val webhookInvocationRepository: WebhookInvocationRepository = mock()
    private val webhookRedisRepository: WebhookInvocationRedisRepository = mock()


    @BeforeEach
    fun setUp() {
        webhookService = DefaultWebhookService(webhookInvocationRepository, webhookRedisRepository)
    }

    @Test
    fun queueWebhookShouldSuccess() = runBlocking<Unit> {
        val wallet = createDummyWallet()
        val transaction = createDummyTransaction()
        val webhook = createDummyWebhookInvocation()

        given(webhookInvocationRepository.save(any<WebhookInvocation>())).willReturn(Mono.just(webhook))

        val result = webhookService.queueWebhook(wallet, transaction)

        Assertions.assertThat(result).isEqualTo(webhook)
    }
}