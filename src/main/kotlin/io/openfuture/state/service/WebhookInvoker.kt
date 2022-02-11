package io.openfuture.state.service

import io.openfuture.state.client.OpenApiClient
import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WebhookCallbackResponse
import io.openfuture.state.property.OpenApiProperties
import io.openfuture.state.webhook.WebhookRestClient
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class WebhookInvoker(
    val webhookRestClient: WebhookRestClient,
    val openApiClient: OpenApiClient
) {

    suspend fun invoke(wallet: Wallet, transaction: Transaction) = runBlocking {
        log.info("Invoking webhook ${wallet.webhook}")
        val webhookBody = WebhookCallbackResponse(
            wallet.orderId, transaction.amount,
            wallet.amount,
            wallet.amount - transaction.amount,
            ((wallet.amount.minus(wallet.totalPaid)) > BigDecimal.ZERO).toString(),
            transaction.to,
            "ETH",
            wallet.rate
        )
        log.info("Invoking webhook $webhookBody")
        if (wallet.source == "woocommerce") {
            val requestBody = StateSignRequest(
                wallet.identity.address,
                wallet.orderId.toInt(),
                if (wallet.amount.minus(wallet.totalPaid) > BigDecimal.ZERO) "PROCESSING" else "COMPLETED"
            )
            val signature = openApiClient.getSignature(wallet.identity.address, requestBody)
            signature?.let { webhookRestClient.doPostWoocommerce(requestBody, wallet.webhook, it) } ?: log.warn("Signature was NULL. Skipping webhook invocation...")
        } else {
            webhookRestClient.doPost(wallet.webhook, webhookBody)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(DefaultWalletService::class.java)
    }

    data class StateSignRequest(
        val address: String,
        val order_id: Int,
        val status: String
    )

}