package io.openfuture.state.webhook.dto

data class WebhookPayload(
        val blockchain: String,
        val walletAddress: String,
        val transaction: TransactionPayload
)
