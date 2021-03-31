package io.openfuture.state.webhook

import io.openfuture.state.config.WebhookConfig
import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WebhookExecution
import io.openfuture.state.service.TransactionService
import io.openfuture.state.service.WalletService
import io.openfuture.state.service.WebhookExecutionService
import io.openfuture.state.service.WebhookService
import io.openfuture.state.webhook.dto.WebhookPayload
import org.springframework.stereotype.Service

@Service
class DefaultWebhookExecutor(
        private val restClient: WebhookRestClient,
        private val walletService: WalletService,
        private val webhookService: WebhookService,
        private val transactionService: TransactionService,
        private val executionService: WebhookExecutionService,
        private val webhookConfig: WebhookConfig
): WebhookExecutor {

    override suspend fun execute(walletId: String) {
        val wallet = walletService.findById(walletId)
        val scheduledTransaction = webhookService.firstTransaction(wallet)
        val transaction = transactionService.findById(scheduledTransaction.id)

        val response = restClient.doPost(wallet.webhook, WebhookPayload(transaction))

        registerInvocation(response, transaction, scheduledTransaction.attempts)
        if (response.status.is2xxSuccessful) {
            scheduleNextWebhook(wallet)
        }
        else {
            scheduleFailedWebhook(wallet, scheduledTransaction)
        }
    }

    private suspend fun scheduleNextWebhook(wallet: Wallet) {
        walletService.save(wallet.apply {  webhookStatus = WebhookStatus.OK })
        webhookService.scheduleNextWebhook(wallet)
    }

    private suspend fun scheduleFailedWebhook(wallet: Wallet, transaction: ScheduledTransaction) {
        if (transaction.attempts >= webhookConfig.maxAttempts()) {
            walletService.save(wallet.apply {  webhookStatus = WebhookStatus.FAILED })
        }

        webhookService.scheduleFailedWebhook(wallet, transaction)
    }

    private suspend fun registerInvocation(response: WebhookResponse, transaction: Transaction, attempt: Int) {
        val webhookExecution = executionService.findByTransactionId(transaction.id)
                ?: WebhookExecution(transaction)

        webhookExecution.addInvocation(WebhookResult(response, attempt))
        executionService.save(webhookExecution)
    }
}
