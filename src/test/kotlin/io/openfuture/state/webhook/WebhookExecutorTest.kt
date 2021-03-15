package io.openfuture.state.webhook

import com.nhaarman.mockitokotlin2.*
import io.openfuture.state.base.ServiceTests
import io.openfuture.state.config.WebhookConfig
import io.openfuture.state.property.WebhookProperties
import io.openfuture.state.service.TransactionService
import io.openfuture.state.service.WalletService
import io.openfuture.state.service.WebhookExecutionService
import io.openfuture.state.service.WebhookService
import io.openfuture.state.util.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

internal class WebhookExecutorTest: ServiceTests() {

    private lateinit var service: WebhookExecutor
    private val restClient: WebhookRestClient = mock()
    private val walletService: WalletService = spy(mock())
    private val webhookService: WebhookService = spy(mock())
    private val transactionService: TransactionService = spy(mock())
    private val executionService: WebhookExecutionService = spy(mock())
    private val webhookConfig: WebhookConfig = WebhookConfig(WebhookProperties(lockTtl = Duration.ofSeconds(3)),)


    @BeforeEach
    fun setUp() {
        service = DefaultWebhookExecutor(restClient, walletService, webhookService, transactionService, executionService, webhookConfig)
    }

    @Test
    fun executeShouldSuccessAndRescheduleWallet() = runBlocking<Unit> {
        val wallet = createDummyWallet()
        val transaction = createDummyTransaction()
        val scheduledTransaction = createDummyScheduledTransaction()
        val positiveResponse = createDummyPositiveWebhookResponse()
        val execution = createDummyWebhookExecution()

        given(walletService.findByBlockchainAndAddress("EthereumBlockchain", "address")).willReturn(wallet)
        given(transactionService.findByHash("hash")).willReturn(transaction)
        given(webhookService.firstTransaction(wallet)).willReturn(scheduledTransaction)
        given(restClient.doPost(eq("webhook"), any())).willReturn(positiveResponse)
        given(executionService.findByTransactionHash("hash")).willReturn(execution)

        service.execute("[EthereumBlockchain] - [address]")

        verify(executionService, times(1)).findByTransactionHash("hash")
        verify(executionService, times(1)).save(any())
        verify(walletService, times(1)).save(wallet.apply { webhookStatus = WebhookStatus.OK })
        verify(webhookService, times(1)).scheduleNextWebhook(wallet)
    }

    @Test
    fun executeShouldSuccessWithNewExecutionAndRescheduleWallet() = runBlocking<Unit> {
        val wallet = createDummyWallet()
        val transaction = createDummyTransaction()
        val scheduledTransaction = createDummyScheduledTransaction()
        val positiveResponse = createDummyPositiveWebhookResponse()

        given(walletService.findByBlockchainAndAddress("EthereumBlockchain", "address")).willReturn(wallet)
        given(transactionService.findByHash("hash")).willReturn(transaction)
        given(webhookService.firstTransaction(wallet)).willReturn(scheduledTransaction)
        given(restClient.doPost(eq("webhook"), any())).willReturn(positiveResponse)
        given(executionService.findByTransactionHash("hash")).willReturn(null)

        service.execute("[EthereumBlockchain] - [address]")

        verify(executionService, times(1)).findByTransactionHash("hash")
        verify(executionService, times(1)).save(any())
        verify(walletService, times(1)).save(wallet.apply { webhookStatus = WebhookStatus.OK })
        verify(webhookService, times(1)).scheduleNextWebhook(wallet)
    }

    @Test
    fun executeShouldFailAndReScheduleTransactio() = runBlocking<Unit> {
        val wallet = createDummyWallet()
        val transaction = createDummyTransaction()
        val scheduledTransaction = createDummyScheduledTransaction()
        val negativeReasons = createDummyNegativeWebhookResponse()
        val execution = createDummyWebhookExecution()

        given(walletService.findByBlockchainAndAddress("EthereumBlockchain", "address")).willReturn(wallet)
        given(transactionService.findByHash("hash")).willReturn(transaction)
        given(webhookService.firstTransaction(wallet)).willReturn(scheduledTransaction)
        given(restClient.doPost(eq("webhook"), any())).willReturn(negativeReasons)
        given(executionService.findByTransactionHash("hash")).willReturn(execution)

        service.execute("[EthereumBlockchain] - [address]")

        verify(executionService, times(1)).findByTransactionHash("hash")
        verify(executionService, times(1)).save(any())
        verify(webhookService, times(1)).scheduleFailedWebhook(wallet, scheduledTransaction)
    }

    @Test
    fun executeShouldFailCancelWalletSchedule() = runBlocking<Unit> {
        val wallet = createDummyWallet()
        val transaction = createDummyTransaction()
        val scheduledTransaction = createDummyScheduledTransaction("hash", 100)
        val negativeReasons = createDummyNegativeWebhookResponse()
        val execution = createDummyWebhookExecution()

        given(walletService.findByBlockchainAndAddress("EthereumBlockchain", "address")).willReturn(wallet)
        given(transactionService.findByHash("hash")).willReturn(transaction)
        given(webhookService.firstTransaction(wallet)).willReturn(scheduledTransaction)
        given(restClient.doPost(eq("webhook"), any())).willReturn(negativeReasons)
        given(executionService.findByTransactionHash("hash")).willReturn(execution)

        service.execute("[EthereumBlockchain] - [address]")

        verify(executionService, times(1)).findByTransactionHash("hash")
        verify(walletService, times(1)).save(wallet.apply { webhookStatus = WebhookStatus.FAILED })
        verify(executionService, times(1)).save(any())
        verify(webhookService, times(1)).scheduleFailedWebhook(wallet, scheduledTransaction)
    }
}
