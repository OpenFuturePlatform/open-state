package io.openfuture.state.service

import com.nhaarman.mockitokotlin2.*
import io.openfuture.state.base.ServiceTests
import io.openfuture.state.util.createDummyTransaction
import io.openfuture.state.util.createDummyTransactionQueueTask
import io.openfuture.state.util.createDummyWallet
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class WebhookServiceTest : ServiceTests() {

    private lateinit var service: WebhookService
    private val walletQueueService: WalletQueueService = spy(mock())
    private val transactionQueueService: TransactionsQueueService = spy(mock())


    @BeforeEach
    fun setUp() {
        service = DefaultWebhookService(walletQueueService, transactionQueueService)
    }

    @Test
    fun addTransactionShouldAddTransactionAndAddWalletToQueue() = runBlocking<Unit> {
        val transaction = createDummyTransaction(id = "transactionId")
        val wallet = createDummyWallet(id = "walletId")
        val transactionTask = createDummyTransactionQueueTask(transactionId = "transactionId")

        given(walletQueueService.score("walletId")).willReturn(null)
        service.scheduleTransaction(wallet, transaction)

        verify(walletQueueService, times(1),).score("walletId")
        verify(walletQueueService, times(1),).add(eq("walletId"), eq(transactionTask))
    }

    @Test
    fun addTransactionShouldAddTransactionToQueue() = runBlocking<Unit> {
        val transaction = createDummyTransaction(id = "transactionId")
        val wallet = createDummyWallet(id = "walletId")
        val transactionTask = createDummyTransactionQueueTask(transactionId = "transactionId")

        given(walletQueueService.score("walletId")).willReturn(12.0)
        service.scheduleTransaction(wallet, transaction)

        verify(walletQueueService, times(1),).score("walletId")
        verify(walletQueueService, never(),).add(eq("WalletId"), any())
        verify(transactionQueueService, times(1),).add(eq("walletId"), eq(transactionTask))
    }
}
