package io.openfuture.state.service

import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WebhookInvocation

interface WebhookService {

    suspend fun queueWebhook(wallet: Wallet, transaction: Transaction): WebhookInvocation
}
