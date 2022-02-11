package io.openfuture.state.webhook

import io.openfuture.state.domain.TransactionQueueTask
import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WebhookStatus
import io.openfuture.state.property.WebhookProperties
import io.openfuture.state.service.TransactionService
import io.openfuture.state.service.WalletService
import io.openfuture.state.service.WebhookInvocationService
import io.openfuture.state.service.WebhookService
import org.springframework.stereotype.Service

@Service
class DefaultWebhookExecutor(
    private val walletService: WalletService,
    private val webhookService: WebhookService,
    private val transactionService: TransactionService,
    private val restClient: WebhookRestClient,
    private val webhookInvocationService: WebhookInvocationService,
    private val webhookProperties: WebhookProperties
) : WebhookExecutor {

    override suspend fun execute(walletId: String) {
        val wallet = walletService.findById(walletId)
        val transactionTask = webhookService.firstTransaction(walletId)
        val transaction = transactionService.findById(transactionTask.transactionId)

        val response = restClient.doPost(wallet.webhook, WebhookPayloadDto(transaction))
        webhookInvocationService.registerInvocation(wallet, transactionTask, response)

        if (response.status.is2xxSuccessful) {
            scheduleNextTransaction(wallet)
        } else {
            scheduleFailedTransaction(wallet, transactionTask)
        }
    }

    private suspend fun scheduleNextTransaction(wallet: Wallet) {
        walletService.updateWebhookStatus(wallet, WebhookStatus.OK)
        webhookService.rescheduleWallet(wallet)
    }

    private suspend fun scheduleFailedTransaction(wallet: Wallet, transactionTask: TransactionQueueTask) {
        if (transactionTask.attempt >= webhookProperties.maxRetryAttempts()) {
            walletService.updateWebhookStatus(wallet, WebhookStatus.FAILED)
            return
        }

        webhookService.rescheduleTransaction(wallet, transactionTask)
    }

}
