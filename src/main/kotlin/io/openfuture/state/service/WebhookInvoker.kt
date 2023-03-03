package io.openfuture.state.service

import io.openfuture.state.component.open.DefaultOpenApi
import io.openfuture.state.domain.Order
import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WebhookCallbackResponse
import io.openfuture.state.service.dto.Watch
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
            wallet.identity.blockchain
        )
        log.info("Invoking webhook $webhookBody")

        if (order.source == "woocommerce") {
            val woocommerceDto = WebhookPayloadDto.WebhookWoocommerceDto(
                wallet,
                if ((order.paid < order.amount)) "PROCESSING" else "COMPLETED"
            )
            val signature = openApi.generateSignature(wallet.identity.address, woocommerceDto)
            log.info("Invoking webhook signature $signature")
            webhookRestClient.doPostWoocommerce(wallet.webhook, signature, woocommerceDto)
        } else webhookRestClient.doPost(wallet.webhook, WebhookPayloadDto(transaction))

        webhookRestClient.doPost(wallet.webhook, webhookBody)
    }

    suspend fun invoke(webHook: String, transaction: Transaction, watch: Watch) = runBlocking {
        log.info("Invoking webhook $webHook $watch")
//        webhookRestClient.doPost(webHook, WebhookPayloadDto(transaction))
    }

    suspend fun invoke(webHook: String, transaction: Transaction) = runBlocking {
        log.info("Invoking webhook $webHook")
        webhookRestClient.doPost(webHook, WebhookPayloadDto(transaction))
    }

    companion object {
        private val log = LoggerFactory.getLogger(DefaultWalletService::class.java)
    }

}