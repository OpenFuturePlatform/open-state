package io.openfuture.state.service

import com.nhaarman.mockitokotlin2.*
import io.openfuture.state.base.ServiceTests
import io.openfuture.state.domain.WebhookStatus
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.extensions.toMillisDouble
import io.openfuture.state.repository.WebhookQueueRedisRepository
import io.openfuture.state.util.*
import io.openfuture.state.domain.WebhookStatus
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.extensions.toMillisDouble
import io.openfuture.state.property.WebhookProperties
import io.openfuture.state.util.createDummyTransaction
import io.openfuture.state.util.createDummyTransactionQueueTask
import io.openfuture.state.util.createDummyWallet
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.Duration
import java.time.LocalDateTime
import java.time.Duration
import java.time.LocalDateTime

internal class WebhookServiceTest : ServiceTests() {

    private lateinit var service: WebhookService
    private var repository: WebhookQueueRedisRepository = spy(Mockito.mock(WebhookQueueRedisRepository::class.java))
    private val walletQueueService: WalletQueueService = spy(mock())
    private val transactionQueueService: TransactionsQueueService = spy(mock())
    private val webhookProperties: WebhookProperties = WebhookProperties()


    @BeforeEach
    fun setUp() {
        service = DefaultWebhookService(repository)
        service = DefaultWebhookService(walletQueueService, transactionQueueService, webhookProperties)
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
                eq(transactionTask.timestamp.toEpochMilli().toDouble())
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








    @Test
    fun rescheduleWalletShouldReturnBecauseWalletStatusIsFailed() = runBlocking<Unit> {
        val wallet = createDummyWallet(id = "walletId", webhookStatus = WebhookStatus.FAILED)

        service.rescheduleWallet(wallet)
        verify(transactionQueueService, never(),).hasTransactions("walletId")
    }

    @Test
    fun rescheduleWalletShouldCancelWalletSchedule() = runBlocking<Unit> {
        val wallet = createDummyWallet(id = "walletId")

        given(transactionQueueService.hasTransactions("walletId")).willReturn(false)
        service.rescheduleWallet(wallet)

        verify(transactionQueueService, times(1)).hasTransactions("walletId")
        verify(walletQueueService, never()).score("walletId")
        verify(walletQueueService, times(1)).remove("walletId")
        verify(transactionQueueService, times(1)).remove("walletId")
    }

    @Test
    fun rescheduleWalletShouldRescheduleWallet() = runBlocking<Unit> {
        val wallet = createDummyWallet(id = "walletId")
        val transactionTask = createDummyTransactionQueueTask()

        given(transactionQueueService.hasTransactions("walletId")).willReturn(true)
        given(walletQueueService.score("walletId")).willReturn(10000.0)
        given(transactionQueueService.first("walletId")).willReturn(transactionTask)

        service.rescheduleWallet(wallet)

        verify(transactionQueueService, times(1)).hasTransactions("walletId")
        verify(walletQueueService, times(1)).score("walletId")
        verify(walletQueueService, times(1)).incrementScore("walletId", transactionTask.timestamp.toMillisDouble() - 10000.0)
        verify(transactionQueueService, times(1)).setAt("walletId", transactionTask, 0)
    }

    @Test
    fun rescheduleWalletShouldThrowNotFoundException() = runBlocking<Unit> {
        val wallet = createDummyWallet(id = "walletId")
        val transactionTask = createDummyTransactionQueueTask()

        given(transactionQueueService.hasTransactions("walletId")).willReturn(true)
        given(walletQueueService.score("walletId")).willReturn(null)

        org.junit.jupiter.api.Assertions.assertThrows(NotFoundException::class.java) {
            runBlocking {
                service.rescheduleWallet(wallet)
            }
        }
    }

    @Test
    fun rescheduleTransactionShouldReScheduleWalletUsingFibonachiRow() = runBlocking<Unit> {
        val wallet = createDummyWallet(id = "walletId")
        val timestamp = LocalDateTime.now()
        val transaction = createDummyTransactionQueueTask("transactionId", 5, timestamp)
        val result = createDummyTransactionQueueTask("transactionId", 6, timestamp)

        service.rescheduleTransaction(wallet, transaction)

        verify(walletQueueService, times(1)).incrementScore("walletId", 3000.0)
        verify(transactionQueueService, times(1)).setAt("walletId", result, 0)
    }

    @Test
    fun rescheduleTransactionShouldReScheduleWalletUsingDailyDelay() = runBlocking<Unit> {
        val wallet = createDummyWallet(id = "walletId")
        val timestamp = LocalDateTime.now()
        val transactionTask = createDummyTransactionQueueTask("transactionId", 12, timestamp)
        val result = createDummyTransactionQueueTask("transactionId", 13, timestamp)

        service.rescheduleTransaction(wallet, transactionTask)

        verify(walletQueueService, times(1)).incrementScore("walletId", Duration.ofDays(1).toMillis().toDouble())
        verify(transactionQueueService, times(1)).setAt("walletId", result, 0)
    }
}
