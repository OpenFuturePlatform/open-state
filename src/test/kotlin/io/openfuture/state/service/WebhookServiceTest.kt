package io.openfuture.state.service

import com.nhaarman.mockitokotlin2.*
import io.openfuture.state.base.ServiceTests
import io.openfuture.state.domain.WebhookStatus
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.extensions.toMillisDouble
import io.openfuture.state.property.WebhookProperties
import io.openfuture.state.util.createDummyTransaction
import io.openfuture.state.util.createDummyTransactionQueueTask
import io.openfuture.state.util.createDummyWallet
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalDateTime

internal class WebhookServiceTest : ServiceTests() {

    private lateinit var service: WebhookService
    private val walletQueueService: WalletQueueService = spy(mock())
    private val transactionQueueService: TransactionsQueueService = spy(mock())
    private val webhookProperties: WebhookProperties = WebhookProperties()


    @BeforeEach
    fun setUp() {
        service = DefaultWebhookService(walletQueueService, transactionQueueService, webhookProperties)
    }

    @Test
    fun scheduleTransactionShouldAddTransactionAndAddWalletToQueue() = runBlocking<Unit> {
        val transaction = createDummyTransaction(id = "transactionId")
        val wallet = createDummyWallet(id = "walletId")
        val transactionTask = createDummyTransactionQueueTask(transactionId = "transactionId")

        given(walletQueueService.score("walletId")).willReturn(null)
        service.scheduleTransaction(wallet, transaction)

        verify(walletQueueService, times(1),).score("walletId")
        verify(walletQueueService, times(1),).add(eq("walletId"), eq(transactionTask))
    }

    @Test
    fun scheduleTransactionShouldAddTransactionToQueue() = runBlocking<Unit> {
        val transaction = createDummyTransaction(id = "transactionId")
        val wallet = createDummyWallet(id = "walletId")
        val transactionTask = createDummyTransactionQueueTask(transactionId = "transactionId")

        given(walletQueueService.score("walletId")).willReturn(12.0)
        service.scheduleTransaction(wallet, transaction)

        verify(walletQueueService, times(1),).score("walletId")
        verify(walletQueueService, never(),).add(eq("WalletId"), any())
        verify(transactionQueueService, times(1),).add(eq("walletId"), eq(transactionTask))
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
