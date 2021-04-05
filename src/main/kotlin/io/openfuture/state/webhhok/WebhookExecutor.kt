package io.openfuture.state.webhhok

interface WebhookExecutor {

    suspend fun execute(walletId: String)
}
