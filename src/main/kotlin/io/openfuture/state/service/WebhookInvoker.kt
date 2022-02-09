package io.openfuture.state.service

import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WebhookCallbackResponse
import io.openfuture.state.webhook.WebhookRestClient
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class WebhookInvoker(
    val webhookRestClient: WebhookRestClient
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
        webhookRestClient.doPost(wallet.webhook, webhookBody)
    }

    companion object {
        private val log = LoggerFactory.getLogger(DefaultWalletService::class.java)
    }

}