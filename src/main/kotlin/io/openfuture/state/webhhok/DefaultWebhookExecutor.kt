package io.openfuture.state.webhhok

import io.openfuture.state.service.TransactionService
import io.openfuture.state.service.WalletService
import io.openfuture.state.service.WebhookService
import org.springframework.stereotype.Service

@Service
class DefaultWebhookExecutor(
    private val walletService: WalletService,
    private val webhookService: WebhookService,
    private val transactionService: TransactionService,
    private val restClient: WebhookRestClient
) : WebhookExecutor {

    override suspend fun execute(walletId: String) {
        val wallet = walletService.findById(walletId)
        val transactionTask = webhookService.firstTransaction(walletId)
        val transaction = transactionService.findById(transactionTask.transactionId)

        restClient.doPost(wallet.webhook, WebhookPayloadDto(transaction))
    }
}
