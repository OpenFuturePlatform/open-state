package io.openfuture.state.webhook

interface WebhookExecutor {

    suspend fun execute(walletAddress: String)
}
