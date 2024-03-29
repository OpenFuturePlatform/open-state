package io.openfuture.state.webhook

import io.openfuture.state.component.open.DefaultOpenApi
import io.openfuture.state.domain.TransactionQueueTask
import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WalletType
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
    private val webhookProperties: WebhookProperties,
    private val openApi: DefaultOpenApi
) : WebhookExecutor {

    override suspend fun execute(walletId: String) {
        val wallet = walletService.findById(walletId)
        val transactionTask = webhookService.firstTransaction(walletId)
        val transaction = transactionService.findById(transactionTask.transactionId)

        val woocommerceDto = WebhookPayloadDto.WebhookWoocommerceDto(wallet, "PROCESSING")
        val signature = openApi.generateSignature(wallet.identity.address, woocommerceDto)
        val response =
            if (wallet.walletType == WalletType.FOR_ORDER)
                restClient.doPostWoocommerce(wallet.webhook, signature, woocommerceDto)
            else restClient.doPost(wallet.webhook, WebhookPayloadDto(transaction, wallet.userData.userId, wallet.userData))
        webhookInvocationService.registerInvocation(wallet, transactionTask, response)

        if (response.status.is2xxSuccessful) {
            scheduleNextTransaction(wallet)
        } else {
            scheduleFailedTransaction(wallet, transactionTask)
        }
    }

    override suspend fun testExecute(walletId: String) {
        val wallet = walletService.findById(walletId)
        val woocommerceDto = WebhookPayloadDto.WebhookWoocommerceDto(wallet, "PROCESSING")
        val signature = openApi.generateSignature(wallet.identity.address, woocommerceDto)
        restClient.doPostWoocommerce(wallet.webhook, signature, woocommerceDto)
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

    private suspend fun sendWoocommerceWebhook(wallet: Wallet) {
        walletService.updateWebhookStatus(wallet, WebhookStatus.OK)
        webhookService.rescheduleWallet(wallet)
    }

}
