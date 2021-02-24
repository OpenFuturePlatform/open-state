package io.openfuture.state.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import io.openfuture.state.base.ServiceTests
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.WebhookDeadQueueRepository
import io.openfuture.state.util.createDummyScheduledTransaction
import io.openfuture.state.util.createDummyWebhookDeadQueue
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono

internal class WebhookDeadQueueServiceTest: ServiceTests() {

    private lateinit var service: WebhookDeadQueueService
    private val repository: WebhookDeadQueueRepository = mock()


    @BeforeEach
    fun setUp() {
        service = DefaultWebhookDeadQueueService(repository)
    }

    @Test
    fun getTransactionsShouldThrowNotFoundException() {
        given(repository.findByWalletAddress("address")).willReturn(Mono.empty())
        assertThrows(NotFoundException::class.java) {
            runBlocking {
                service.getTransactions("address")
            }
        }
    }

    @Test
    fun getTransactionsShouldReturnListOfTransactions() = runBlocking<Unit> {
        val deadQueue = createDummyWebhookDeadQueue()

        given(repository.findByWalletAddress("address")).willReturn(Mono.just(deadQueue))

        val result = service.getTransactions("address")
        Assertions.assertThat(result).isEqualTo(deadQueue.getTransactions())
    }

    @Test
    fun addTransactionToNonExistingWalletShouldReturnProperValue() = runBlocking<Unit> {
        val deadQueue = createDummyWebhookDeadQueue("address", emptyList())
        val scheduledTransaction = createDummyScheduledTransaction()

        given(repository.findByWalletAddress("address")).willReturn(Mono.empty())
        given(repository.save(any())).willReturn(Mono.just(deadQueue))


        val result = service.addTransactions("address", listOf(scheduledTransaction))
        Assertions.assertThat(result).isEqualTo(deadQueue)
    }

    @Test
    fun addTransactionShouldReturnProperValue() = runBlocking<Unit> {
        val deadQueue = createDummyWebhookDeadQueue("address", emptyList())
        val scheduledTransaction = createDummyScheduledTransaction()

        given(repository.findByWalletAddress("address")).willReturn(Mono.just(deadQueue))
        given(repository.save(any())).willReturn(Mono.just(deadQueue))

        val result = service.addTransactions("address", listOf(scheduledTransaction))
        Assertions.assertThat(result).isEqualTo(deadQueue)
    }
}