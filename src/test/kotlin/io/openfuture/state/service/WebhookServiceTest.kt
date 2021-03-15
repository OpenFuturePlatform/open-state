package io.openfuture.state.service

import com.nhaarman.mockitokotlin2.*
import io.openfuture.state.base.ServiceTests
import io.openfuture.state.config.WebhookConfig
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.property.WebhookProperties
import io.openfuture.state.util.createDummyScheduledTransaction
import io.openfuture.state.util.createDummyTransaction
import io.openfuture.state.util.createDummyWallet
import io.openfuture.state.util.toEpochMilli
import io.openfuture.state.webhook.WebhookStatus
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalDateTime

internal class WebhookServiceTest: ServiceTests() {

    private lateinit var service: WebhookService
    private val walletService: WalletWebhookQueueService = spy(mock())
    private val transactionService: TransactionsQueueService = spy(mock())
    private val deadQueueService: WebhookDeadQueueService = spy(mock())
    private val webhookConfig: WebhookConfig = WebhookConfig(WebhookProperties(lockTtl = Duration.ofSeconds(3)),)


    @BeforeEach
    fun setUp() {
        service = DefaultWebhookService(walletService, transactionService, deadQueueService, webhookConfig)
    }

    @Test
    fun addTransactionShouldAddTransactionToDeadQueue() = runBlocking<Unit> {
        val transaction = createDummyTransaction("hash")
        val wallet = createDummyWallet("chain", "address", "webhook", WebhookStatus.FAILED)

        service.addTransaction(wallet, transaction)

        verify(deadQueueService, times(1),).addTransactions(eq("[chain] - [address]"), any())
        verify(walletService, never(),).score("[chain] - [address]")
        verify(transactionService, never(),).add(eq("[chain] - [address]"), any())
    }

    @Test
    fun addTransactionShouldAddTransactionAndAddWalletToQueue() = runBlocking<Unit> {
        val transaction = createDummyTransaction("hash")
        val wallet = createDummyWallet("chain", "address")

        given(walletService.score("[chain] - [address]")).willReturn(null)
        service.addTransaction(wallet, transaction)

        verify(walletService, times(1),).score("[chain] - [address]")
        verify(walletService, times(1),).add(eq("[chain] - [address]"), any())
    }

    @Test
    fun addTransactionShouldAddTransactionToQueue() = runBlocking<Unit> {
        val transaction = createDummyTransaction("hash")
        val wallet = createDummyWallet("chain", "address")

        given(walletService.score("[chain] - [address]")).willReturn(12.0)
        service.addTransaction(wallet, transaction)

        verify(walletService, times(1),).score("[chain] - [address]")
        verify(walletService, never(),).add(eq("[chain] - [address]"), any())
        verify(transactionService, times(1),).add(eq("[chain] - [address]"), any())
    }

    @Test
    fun scheduleNextWebhookShouldReturnBecauseWalletStatusIsFailed() = runBlocking<Unit> {
        val wallet = createDummyWallet("chain", "address", "webhook", WebhookStatus.FAILED)

        service.scheduleNextWebhook(wallet)

        verify(transactionService, never(),).hasTransactions("address")
    }

    @Test
    fun scheduleNextWebhookShouldCancelWalletSchedule() = runBlocking<Unit> {
        val wallet = createDummyWallet()

        given(transactionService.hasTransactions("[EthereumBlockchain] - [address]")).willReturn(false)
        service.scheduleNextWebhook(wallet)

        verify(transactionService, times(1)).hasTransactions("[EthereumBlockchain] - [address]")
        verify(walletService, never()).score("[EthereumBlockchain] - [address]")
        verify(walletService, times(1)).remove("[EthereumBlockchain] - [address]")
        verify(transactionService, times(1)).remove("[EthereumBlockchain] - [address]")
    }

    @Test
    fun scheduleNextWebhookShouldReScheduleWalletWebhook() = runBlocking<Unit> {
        val wallet = createDummyWallet()
        val transaction = createDummyScheduledTransaction()

        given(transactionService.hasTransactions("[EthereumBlockchain] - [address]")).willReturn(true)
        given(walletService.score("[EthereumBlockchain] - [address]")).willReturn(10000.0)
        given(transactionService.first("[EthereumBlockchain] - [address]")).willReturn(transaction)

        service.scheduleNextWebhook(wallet)

        verify(transactionService, times(1)).hasTransactions("[EthereumBlockchain] - [address]")
       verify(walletService, times(1)).score("[EthereumBlockchain] - [address]")
        verify(walletService, times(1))
                .incrementScore("[EthereumBlockchain] - [address]", transaction.timestamp.toEpochMilli().toDouble() - 10000.0)
        verify(transactionService, times(1)).setAt("[EthereumBlockchain] - [address]", transaction, 0)
    }

    @Test
    fun scheduleNextWebhookShouldThrowNotFoundException() = runBlocking<Unit> {
        val wallet = createDummyWallet()
        val transaction = createDummyScheduledTransaction()

        given(transactionService.hasTransactions("[EthereumBlockchain] - [address]")).willReturn(true)
        given(walletService.score("[EthereumBlockchain] - [address]")).willReturn(null)

        org.junit.jupiter.api.Assertions.assertThrows(NotFoundException::class.java) {
            runBlocking {
                service.scheduleNextWebhook(wallet)
            }
        }
    }

    @Test
    fun scheduleFailedWebhookShouldReScheduleWalletUsingFibonachiRow() = runBlocking<Unit> {
        val wallet = createDummyWallet()
        val timestamp = LocalDateTime.now()
        val transaction = createDummyScheduledTransaction("hash", 5, timestamp)
        val result = createDummyScheduledTransaction("hash", 6, timestamp)

        service.scheduleFailedWebhook(wallet, transaction)

        verify(walletService, times(1)).incrementScore("[EthereumBlockchain] - [address]", 3000.0)
        verify(transactionService, times(1)).setAt("[EthereumBlockchain] - [address]", result, 0)
    }

    @Test
    fun scheduleFailedWebhookShouldReScheduleWalletUsingDailyDelay() = runBlocking<Unit> {
        val wallet = createDummyWallet()
        val timestamp = LocalDateTime.now()
        val transaction = createDummyScheduledTransaction("hash", 12, timestamp)
        val result = createDummyScheduledTransaction("hash", 13, timestamp)

        service.scheduleFailedWebhook(wallet, transaction)

        verify(walletService, times(1)).incrementScore("[EthereumBlockchain] - [address]", Duration.ofDays(1).toMillis().toDouble())
        verify(transactionService, times(1)).setAt("[EthereumBlockchain] - [address]", result, 0)
    }

    @Test
    fun scheduleFailedWebhookShouldAddTransactionsToDeadQueue() = runBlocking<Unit> {
        val wallet = createDummyWallet()
        val transaction = createDummyScheduledTransaction("hash", 27, LocalDateTime.now())
        val transaction1 = createDummyScheduledTransaction()
        val transaction2 = createDummyScheduledTransaction()

        given(transactionService.findAll("[EthereumBlockchain] - [address]")).willReturn(listOf(transaction1, transaction2))
        service.scheduleFailedWebhook(wallet, transaction)

        verify(transactionService, times(1)).findAll("[EthereumBlockchain] - [address]")
        verify(deadQueueService, times(1)).addTransactions("[EthereumBlockchain] - [address]", listOf(transaction, transaction1, transaction2))
        verify(walletService, times(1)).remove("[EthereumBlockchain] - [address]")
        verify(transactionService, times(1)).remove("[EthereumBlockchain] - [address]")
    }

    @Test
    fun addTransactionsFrmDeadQueueShouldReturnNoTransactions() = runBlocking<Unit> {
        val wallet = createDummyWallet()

        given(deadQueueService.hasTransactions("[EthereumBlockchain] - [address]")).willReturn(false)
        service.addTransactionsFromDeadQueue(wallet)

        verify(deadQueueService, times(1)).hasTransactions("[EthereumBlockchain] - [address]")
        verify(deadQueueService, never()).getTransactions("[EthereumBlockchain] - [address]")
    }

    @Test
    fun addTransactionsFrmDeadQueueShouldAddWalletAndTransactionsToQueue() = runBlocking<Unit> {
        val wallet = createDummyWallet()
        val transaction1 = createDummyScheduledTransaction()
        val transaction2 = createDummyScheduledTransaction()

        given(deadQueueService.hasTransactions("[EthereumBlockchain] - [address]")).willReturn(true)
        given(deadQueueService.getTransactions("[EthereumBlockchain] - [address]")).willReturn(listOf(transaction1, transaction2))
        given(walletService.score("[EthereumBlockchain] - [address]")).willReturn(null)

        service.addTransactionsFromDeadQueue(wallet)

        verify(walletService, times(1)).add("[EthereumBlockchain] - [address]", transaction1)
        verify(transactionService, times(1)).add("[EthereumBlockchain] - [address]", transaction2)
        verify(deadQueueService, times(1)).remove("[EthereumBlockchain] - [address]")
    }

    @Test
    fun addTransactionsFrmDeadQueueShouldAddTransactionsToQueue() = runBlocking<Unit> {
        val wallet = createDummyWallet()
        val transaction1 = createDummyScheduledTransaction("hash1")
        val transaction2 = createDummyScheduledTransaction("hash2")

        given(deadQueueService.hasTransactions("[EthereumBlockchain] - [address]")).willReturn(true)
        given(deadQueueService.getTransactions("[EthereumBlockchain] - [address]")).willReturn(listOf(transaction1, transaction2))
        given(walletService.score("[EthereumBlockchain] - [address]")).willReturn(1.0)

        service.addTransactionsFromDeadQueue(wallet)

        verify(walletService, never()).add("[EthereumBlockchain] - [address]", transaction1)
        verify(transactionService, times(1)).add("[EthereumBlockchain] - [address]", transaction1)
        verify(transactionService, times(1)).add("[EthereumBlockchain] - [address]", transaction2)
        verify(deadQueueService, times(1)).remove("[EthereumBlockchain] - [address]")
    }
}
