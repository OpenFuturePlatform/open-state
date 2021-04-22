package io.openfuture.state.service

import com.nhaarman.mockitokotlin2.*
import io.openfuture.state.base.ServiceTests
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.WebhookQueueRedisRepository
import io.openfuture.state.util.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito

internal class WebhookServiceTest : ServiceTests() {

    private lateinit var service: WebhookService
    private var repository: WebhookQueueRedisRepository = spy(Mockito.mock(WebhookQueueRedisRepository::class.java))


    @BeforeEach
    fun setUp() {
        service = DefaultWebhookService(repository)
    }

    @Test
    fun scheduleTransactionShouldAddTransactionAndAddWalletToQueue() = runBlocking<Unit> {
        val wallet = createDummyWallet(id = "walletId")
        val transaction = createDummyTransaction(id = "transactionId")
        val transactionTask = createDummyTransactionQueueTask(transactionId = "transactionId")

        given(repository.walletScore("walletId")).willReturn(null)

        service.scheduleTransaction(wallet, transaction)

        verify(repository, times(1)).walletScore("walletId")
        verify(repository, times(1))
            .addWallet(
                eq("walletId"),
                eq(transactionTask),
                eq(transactionTask.timestamp.toEpochMillis().toDouble())
            )
    }

    @Test
    fun scheduleTransactionShouldAddTransactionToQueue() = runBlocking {
        val wallet = createDummyWallet(id = "walletId")
        val transaction = createDummyTransaction(id = "transactionId")
        val transactionTask = createDummyTransactionQueueTask(transactionId = "transactionId", transaction.date)

        given(repository.walletScore("walletId")).willReturn(12.0)
        service.scheduleTransaction(wallet, transaction)

        verify(repository, times(1)).walletScore("walletId")
        verify(repository, never()).addWallet(eq("WalletId"), any(), any())
        verify(repository, times(1)).addTransaction(eq("walletId"), eq(transactionTask))
    }

    @Test
    fun firstWalletInQueueShouldReturnNull() = runBlocking {
        given(repository.firstWalletInScoreRange(eq(null), any())).willReturn(null)
        val result = service.firstWalletInQueue()

        assertThat(result).isNull()
    }

    @Test
    fun firstWalletInQueueShouldReturnProperValue() = runBlocking<Unit> {
        val walletTask = createDummyWalletQueueTask("walletId", 5.0)

        given(repository.firstWalletInScoreRange(eq(null), any())).willReturn("walletId")
        given(repository.walletScore(eq("walletId"))).willReturn(5.0)

        val result = service.firstWalletInQueue()

        assertThat(result).isEqualTo(walletTask)
    }

    @Test
    fun firstWalletInQueueShouldReturnNullInSpecifiedRange() = runBlocking {
        given(repository.firstWalletInScoreRange(eq(1000.0), any())).willReturn(null)
        val result = service.firstWalletInQueue(1000.0)

        assertThat(result).isNull()
    }

    @Test
    fun firstWalletInQueueShouldReturnProperValueInSpecifiedRange() = runBlocking<Unit> {
        val walletTask = createDummyWalletQueueTask("walletId", 5.0)

        given(repository.firstWalletInScoreRange(eq(1000.0), any())).willReturn("walletId")
        given(repository.walletScore(eq("walletId"))).willReturn(5.0)

        val result = service.firstWalletInQueue(1000.0)

        assertThat(result).isEqualTo(walletTask)
    }

    @Test
    fun firstTransactionShouldThrowNotFoundException() = runBlocking<Unit> {
        given(repository.firstTransaction("walletId")).willReturn(null)
        assertThrows(NotFoundException::class.java) {
            runBlocking {
                service.firstTransaction("walletId")
            }
        }
    }

    @Test
    fun firstTransactionShouldReturnProperValue() = runBlocking<Unit> {
        val transactionTask = createDummyTransactionQueueTask()

        given(repository.firstTransaction("walletId")).willReturn(transactionTask)

        val result = service.firstTransaction("walletId")
        assertThat(result).isEqualTo(transactionTask)
    }

}
