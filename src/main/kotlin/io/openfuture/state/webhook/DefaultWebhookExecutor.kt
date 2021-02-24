package io.openfuture.state.webhook

import io.openfuture.state.config.WebhookConfig
import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WebhookExecution
import io.openfuture.state.service.*
import io.openfuture.state.webhook.dto.TransactionPayload
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

    override suspend fun execute(walletAddress: String) {
        val wallet = walletService.findByAddress(walletAddress)
        val scheduledTransaction = webhookService.firstTransaction(wallet)
        val transaction = transactionService.findByHash(scheduledTransaction.hash)

        val response = restClient.doPost(
                wallet.webhook,
                createWebhookPayload(wallet, transaction)
        )


        addWebhookInvocation(wallet, response, scheduledTransaction)
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

    private suspend fun addWebhookInvocation(wallet: Wallet, response: WebhookResponse, transaction: ScheduledTransaction) {
        val webhookExecution = executionService
                .findByTransactionHash(transaction.hash) ?:
                        WebhookExecution(
                                walletAddress = wallet.address,
                                transactionHash = transaction.hash
                        )

        val invocation = WebhookResult(
                status = response.status,
                url = response.url,
                attempt = transaction.attempts,
                message = response.message
        )

        webhookExecution.addInvocation(invocation)
        executionService.save(webhookExecution)
    }

    private fun createWebhookPayload(wallet: Wallet, transaction: Transaction): WebhookPayload {
        val transactionPayload = TransactionPayload(
                hash = transaction.hash,
                from = transaction.from,
                to = transaction.to,
                amount = transaction.amount,
                date = transaction.date,
                blockHeight = transaction.blockHeight,
                blockHash = transaction.blockHash
        )

        return  WebhookPayload(
                blockchain =  wallet.blockchain,
                walletAddress = wallet.address,
                transactionPayload
        )
    }
}
