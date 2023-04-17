package io.openfuture.state.service

import io.openfuture.state.component.open.DefaultOpenApi
import io.openfuture.state.domain.*
import io.openfuture.state.webhook.WebhookPayloadDto
import io.openfuture.state.webhook.WebhookRestClient
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class WebhookInvoker(
    val webhookRestClient: WebhookRestClient,
    private val openApi: DefaultOpenApi
) {

    suspend fun invoke(wallet: Wallet, transaction: Transaction, order: Order) = runBlocking {
        log.info("Invoking webhook ${wallet.webhook}")
        val webhookBody = WebhookCallbackResponse(
            order.orderKey,
            transaction.amount,//in crypto
            order.amount,//USD
            order.amount - order.paid, //0,0006 - 0.001 = -0.0003219200
            (order.paid >= order.amount).toString(), //0.0006 - 0.001 > -0.0003219200
            transaction.to,
            wallet.identity.blockchain,
            wallet.userData
        )
        log.info("Invoking webhook $webhookBody")

        if (wallet.walletType == WalletType.FOR_ORDER) {
            val woocommerceDto = WebhookPayloadDto.WebhookWoocommerceDto(
                wallet,
                if ((order.paid < order.amount)) "PROCESSING" else "COMPLETED"
            )
            val signature = openApi.generateSignature(wallet.identity.address, woocommerceDto)
            log.info("Invoking webhook signature $signature")
            webhookRestClient.doPostWoocommerce(wallet.webhook, signature, woocommerceDto)
        } else webhookRestClient.doPost(wallet.webhook, WebhookPayloadDto(transaction, userId = wallet.userData.userId, metadata = wallet.userData))

        webhookRestClient.doPost(wallet.webhook, webhookBody)
    }

    suspend fun invoke(webHook: String, transaction: Transaction, metadata: Any, userId: String?) = runBlocking {
        log.info("Invoking webhook $webHook $metadata $userId $transaction")
        webhookRestClient.doPost(webHook, WebhookPayloadDto(transaction, userId, metadata))
    }

    companion object {
        private val log = LoggerFactory.getLogger(DefaultWalletService::class.java)
    }

}