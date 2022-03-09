package io.openfuture.state.service

import io.openfuture.state.component.open.DefaultOpenApi
import io.openfuture.state.domain.Order
import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WebhookCallbackResponse
import io.openfuture.state.webhook.WebhookPayloadDto
import io.openfuture.state.webhook.WebhookRestClient
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class WebhookInvoker(
    val webhookRestClient: WebhookRestClient,
    private val openApi: DefaultOpenApi
) {

    suspend fun invoke(wallet: Wallet, transaction: Transaction, order: Order) = runBlocking {
        log.info("Invoking webhook ${order.webhook}")
        val webhookBody = WebhookCallbackResponse(
            order.orderId, transaction.amount,
            order.amount,//0,0006
            order.amount - transaction.amount, //0,0006 - 0.001 = -0.0003219200
            "BigDecimal.ZERO", //0.0006 - 0.001 > -0.0003219200
            transaction.to,
            "ETH",
            wallet.rate
        )
        log.info("Invoking webhook $webhookBody")

        if (order.source == "woocommerce") {
            val woocommerceDto = WebhookPayloadDto.WebhookWoocommerceDto(
                wallet,
                if (order.amount.minus(order.amount - transaction.amount) > BigDecimal.ZERO) "PROCESSING" else "COMPLETED"
            )
            val signature = openApi.generateSignature(wallet.identity.address, woocommerceDto)
            log.info("Invoking webhook signature $signature")
            webhookRestClient.doPostWoocommerce(order.webhook, signature, woocommerceDto)
        } else webhookRestClient.doPost(order.webhook, WebhookPayloadDto(transaction))

        webhookRestClient.doPost(order.webhook, webhookBody)
    }

    companion object {
        private val log = LoggerFactory.getLogger(DefaultWalletService::class.java)
    }

}