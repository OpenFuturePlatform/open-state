package io.openfuture.state.webhook

import com.nhaarman.mockitokotlin2.*
import io.openfuture.state.base.ServiceTests
import io.openfuture.state.component.open.DefaultOpenApi
import io.openfuture.state.domain.WebhookStatus
import io.openfuture.state.property.WebhookProperties
import io.openfuture.state.service.TransactionService
import io.openfuture.state.service.WalletService
import io.openfuture.state.service.WebhookInvocationService
import io.openfuture.state.service.WebhookService
import io.openfuture.state.util.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

internal class WebhookExecutorTest : ServiceTests() {

    private lateinit var service: WebhookExecutor
    private val restClient: WebhookRestClient = mock()
    private val walletService: WalletService = spy(mock())
    private val webhookService: WebhookService = spy(mock())
    private val transactionService: TransactionService = spy(mock())
    private val invocationService: WebhookInvocationService = spy(mock())
    private val openApi : DefaultOpenApi = spy(mock())
    private val webhookProperties: WebhookProperties = WebhookProperties(lockTTL = Duration.ofSeconds(3))


    @BeforeEach
    fun setUp() {
        service = DefaultWebhookExecutor(
            walletService,
            webhookService,
            transactionService,
            restClient,
            invocationService,
            webhookProperties,
            openApi
        )
    }

    @Test
    fun executeShouldSuccessAndRescheduleWallet() = runBlocking {
        val wallet = createDummyWallet(id = "walletId")
        val transaction = createDummyTransaction(id = "transactionId")
        val transactionTask = createDummyTransactionQueueTask()
        val positiveResponse = createDummyPositiveWebhookResponse()

        given(walletService.findById("walletId")).willReturn(wallet)
        given(transactionService.findById("transactionId")).willReturn(transaction)
        given(webhookService.firstTransaction("walletId")).willReturn(transactionTask)
        given(restClient.doPost(eq("webhook"), any())).willReturn(positiveResponse)

        service.execute("walletId")

        verify(invocationService, times(1)).registerInvocation(wallet, transactionTask, positiveResponse)
        verify(walletService, times(1)).updateWebhookStatus(wallet, WebhookStatus.OK)
        verify(webhookService, times(1)).rescheduleWallet(wallet)
    }

    @Test
    fun executeShouldFailAndRescheduleTransaction() = runBlocking<Unit> {
        val wallet = createDummyWallet(id = "walletId")
        val transaction = createDummyTransaction(id = "transactionId")
        val transactionTask = createDummyTransactionQueueTask()
        val negativeResponse = createDummyNegativeWebhookResponse()

        given(walletService.findById("walletId")).willReturn(wallet)
        given(transactionService.findById("transactionId")).willReturn(transaction)
        given(webhookService.firstTransaction("walletId")).willReturn(transactionTask)
        given(restClient.doPost(eq("webhook"), any())).willReturn(negativeResponse)

        service.execute("walletId")

        verify(invocationService, times(1)).registerInvocation(wallet, transactionTask, negativeResponse)
        verify(webhookService, times(1)).rescheduleTransaction(wallet, transactionTask)
    }

    @Test
    fun executeShouldFailCancelWalletSchedule() = runBlocking<Unit> {
        val wallet = createDummyWallet(id = "walletId")
        val transaction = createDummyTransaction(id = "transactionId")
        val transactionTask = createDummyTransactionQueueTask(transactionId = "transactionId", attempt = 50)
        val negativeResponse = createDummyNegativeWebhookResponse()

        given(walletService.findById("walletId")).willReturn(wallet)
        given(transactionService.findById("transactionId")).willReturn(transaction)
        given(webhookService.firstTransaction("walletId")).willReturn(transactionTask)
        given(restClient.doPost(eq("webhook"), any())).willReturn(negativeResponse)

        service.execute("walletId")

        verify(walletService, times(1)).updateWebhookStatus(wallet, WebhookStatus.FAILED)
        verify(invocationService, times(1)).registerInvocation(wallet, transactionTask, negativeResponse)
    }

}
