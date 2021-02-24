package io.openfuture.state.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import io.openfuture.state.base.ServiceTests
import io.openfuture.state.repository.WebhookExecutionRepository
import io.openfuture.state.util.createDummyWebhookExecution
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono

internal class WebhookExecutionServiceTest: ServiceTests() {

    private lateinit var service: WebhookExecutionService
    private val repository: WebhookExecutionRepository = mock()


    @BeforeEach
    fun setUp() {
        service = DefaultWebhookExecutionService(repository)
    }

    @Test
    fun findByTransactionHashShouldReturnNull() = runBlocking<Unit> {
        given(repository.findByTransactionHash("hash")).willReturn(Mono.empty())

        val result = service.findByTransactionHash("hash")
        Assertions.assertThat(result).isNull()
    }

    @Test
    fun findByTransactionHashShouldReturnProperValue() = runBlocking<Unit> {
        val execution = createDummyWebhookExecution()

        given(repository.findByTransactionHash("hash")).willReturn(Mono.just(execution))
        val result = service.findByTransactionHash("hash")

        Assertions.assertThat(result).isEqualTo(execution)
    }

    @Test
    fun saveShouldSaveExecution() = runBlocking<Unit> {
        val execution = createDummyWebhookExecution()

        given(repository.save(any())).willReturn(Mono.just(execution))
        val result = service.save(execution)

        Assertions.assertThat(result).isEqualTo(execution)
    }

}
