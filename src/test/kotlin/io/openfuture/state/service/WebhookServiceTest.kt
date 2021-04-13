package io.openfuture.state.service

import com.nhaarman.mockitokotlin2.*
import io.openfuture.state.base.ServiceTests
import io.openfuture.state.domain.WebhookStatus
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.property.WebhookProperties
import io.openfuture.state.repository.WebhookQueueRedisRepository
import io.openfuture.state.util.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.Duration
import java.time.LocalDateTime

internal class WebhookServiceTest : ServiceTests() {

    private lateinit var service: WebhookService
    private var repository: WebhookQueueRedisRepository = spy(Mockito.mock(WebhookQueueRedisRepository::class.java))
    private var deadQueueService: TransactionDeadQueueService = spy(mock())
    private val webhookProperties: WebhookProperties = WebhookProperties()


    @BeforeEach
    fun setUp() {
        service = DefaultWebhookService(repository, deadQueueService, webhookProperties)
    }

    @Test
    fun scheduleTransactionShouldAddTransactionToDeadQeue() = runBlocking {
        val wallet = createDummyWallet(blockchain = "Ethereum", address = "address", webhookStatus = WebhookStatus.FAILED)
        val transaction = createDummyTransaction(id = "transactionId")
        val transactionTask = createDummyTransactionQueueTask(transactionId = "transactionId")

        service.scheduleTransaction(wallet, transaction)

        verify(deadQueueService, times(1)).addTransactionToDeadQueue(wallet.identity, listOf(transactionTask))
        verify(repository, never()).addWallet(any(), any(), any())
        verify(repository, never()).addTransactions(any(), any())
    }

    @Test
    fun scheduleTransactionShouldAddTransactionFromDeadQueue() = runBlocking<Unit> {
        val wallet = createDummyWallet(blockchain = "Ethereum", address = "address", id = "walletId")
        val transaction = createDummyTransaction(id = "transactionId2")
        val transactionTask1 = createDummyTransactionQueueTask(transactionId = "transactionId1")
        val transactionTask2 = createDummyTransactionQueueTask(transactionId = "transactionId2")

        given(deadQueueService.getTransactionFromDeadQueue(wallet.identity)).willReturn(listOf(transactionTask1))
        given(deadQueueService.hasTransactions(wallet.identity)).willReturn(true)
        given(repository.walletScore("walletId")).willReturn(null)

        service.scheduleTransaction(wallet, transaction)

        verify(deadQueueService, times(1)).getTransactionFromDeadQueue(eq(wallet.identity))
        verify(deadQueueService, times(1)).hasTransactions(eq(wallet.identity))
        verify(deadQueueService, times(1)).removeFromDeadQueue(eq(wallet.identity))
        verify(repository, times(1)).addWallet(eq("walletId"), eq(listOf(transactionTask1, transactionTask2)), any())
        verify(repository, times(1)).walletScore(eq("walletId"))
    }

    @Test
    fun scheduleTransactionShouldAddTransactionAndAddWalletToQueue() = runBlocking<Unit> {
        val wallet = createDummyWallet(id = "walletId")
        val transaction = createDummyTransaction(id = "transactionId")
        val transactionTask = createDummyTransactionQueueTask(transactionId = "transactionId")

        given(deadQueueService.getTransactionFromDeadQueue(wallet.identity)).willReturn(emptyList())
        given(deadQueueService.hasTransactions(wallet.identity)).willReturn(false)
        given(repository.walletScore("walletId")).willReturn(null)

        service.scheduleTransaction(wallet, transaction)

        verify(repository, times(1)).walletScore("walletId")
        verify(repository, times(1))
            .addWallet(
                eq("walletId"),
                eq(listOf(transactionTask)),
                eq(transactionTask.timestamp.toEpochMillis().toDouble())
            )
    }

    @Test
    fun scheduleTransactionShouldAddTransactionToQueue() = runBlocking {
        val wallet = createDummyWallet(id = "walletId")
        val transaction = createDummyTransaction(id = "transactionId")
        val transactionTask = createDummyTransactionQueueTask(transactionId = "transactionId", transaction.date)

        given(repository.walletScore("walletId")).willReturn(12.0)
        given(deadQueueService.getTransactionFromDeadQueue(wallet.identity)).willReturn(emptyList())
        given(deadQueueService.hasTransactions(wallet.identity)).willReturn(false)

        service.scheduleTransaction(wallet, transaction)

        verify(repository, times(1)).walletScore("walletId")
        verify(repository, never()).addWallet(eq("WalletId"), any(), any())
        verify(repository, times(1)).addTransactions(eq("walletId"), eq(listOf(transactionTask)))
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
        verify(repository, never(),).transactionsCount("walletId")
    }

    @Test
    fun rescheduleWalletShouldCancelWalletSchedule() = runBlocking {
        val wallet = createDummyWallet(id = "walletId")

        given(repository.transactionsCount("walletId")).willReturn(0)
        service.rescheduleWallet(wallet)

        verify(repository, times(1)).transactionsCount("walletId")
        verify(repository, never()).walletScore("walletId")
        verify(repository, times(1)).removeWalletFromQueue("walletId")
    }

    @Test
    fun rescheduleWalletShouldRescheduleWallet() = runBlocking<Unit> {
        val wallet = createDummyWallet(id = "walletId")
        val transactionTask = createDummyTransactionQueueTask()

        given(repository.transactionsCount("walletId")).willReturn(10)
        given(repository.walletScore("walletId")).willReturn(10000.0)
        given(repository.firstTransaction("walletId")).willReturn(transactionTask)

        service.rescheduleWallet(wallet)

        verify(repository, times(1)).transactionsCount("walletId")
        verify(repository, times(1)).walletScore("walletId")
        verify(repository, times(1)).changeScore("walletId", transactionTask.timestamp.toEpochMillis() - 10000.0)
        verify(repository, times(1)).setTransactionAtIndex("walletId", transactionTask, 0)
    }

    @Test
    fun rescheduleWalletShouldThrowNotFoundException() = runBlocking<Unit> {
        val wallet = createDummyWallet(id = "walletId")

        given(repository.transactionsCount("walletId")).willReturn(10)
        given(repository.walletScore("walletId")).willReturn(null)

        assertThrows(NotFoundException::class.java) {
            runBlocking {
                service.rescheduleWallet(wallet)
            }
        }
    }

    @Test
    fun rescheduleTransactionShouldReScheduleWalletUsingFibonachiRow() = runBlocking<Unit> {
        val wallet = createDummyWallet(id = "walletId")
        val timestamp = LocalDateTime.now()
        val transaction = createDummyTransactionQueueTask("transactionId", timestamp, 5)
        val result = createDummyTransactionQueueTask("transactionId", timestamp, 6)

        service.rescheduleTransaction(wallet, transaction)

        verify(repository, times(1)).changeScore("walletId", 3000.0)
        verify(repository, times(1)).setTransactionAtIndex("walletId", result, 0)
    }

    @Test
    fun rescheduleTransactionShouldReScheduleWalletUsingDailyDelay() = runBlocking<Unit> {
        val wallet = createDummyWallet(id = "walletId")
        val timestamp = LocalDateTime.now()
        val transactionTask = createDummyTransactionQueueTask("transactionId", timestamp, 12)
        val result = createDummyTransactionQueueTask("transactionId", timestamp, 13)

        service.rescheduleTransaction(wallet, transactionTask)

        verify(repository, times(1)).changeScore("walletId", Duration.ofDays(1).toMillis().toDouble())
        verify(repository, times(1)).setTransactionAtIndex("walletId", result, 0)
    }

}
