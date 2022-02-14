package io.openfuture.state.service

import io.openfuture.state.component.open.DefaultOpenApi
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

    suspend fun invoke(wallet: Wallet, transaction: Transaction) = runBlocking {
        log.info("Invoking webhook ${wallet.webhook}")
        val webhookBody = WebhookCallbackResponse(
            wallet.orderId,transaction.amount,
            wallet.amount,
            wallet.amount - transaction.amount,
            ((wallet.amount.minus(wallet.totalPaid)).compareTo(BigDecimal.ZERO) > 0).toString(),
            transaction.to,
            "ETH",
            wallet.rate
        )
        log.info("Invoking webhook $webhookBody")

        if(wallet.source == "woocommerce"){
            val woocommerceDto = WebhookPayloadDto.WebhookWoocommerceDto(wallet, "PROCESSING")
            val signature = openApi.generateSignature(wallet.identity.address, woocommerceDto)
            log.info("Invoking webhook signature $signature")
            webhookRestClient.doPostWoocommerce(wallet.webhook, signature, woocommerceDto)
        }  else  webhookRestClient.doPost(wallet.webhook, WebhookPayloadDto(transaction))

        webhookRestClient.doPost(wallet.webhook, webhookBody)
    }

    companion object {
        private val log = LoggerFactory.getLogger(DefaultWalletService::class.java)
    }

}