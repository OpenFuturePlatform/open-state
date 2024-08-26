package io.openfuture.state.service

import io.openfuture.state.domain.transaction.TransactionQueueTask
import io.openfuture.state.domain.wallet.Wallet
import io.openfuture.state.webhook.WebhookRestClient

interface WebhookInvocationService {

    suspend fun registerInvocation(
        wallet: Wallet,
        transactionTask: TransactionQueueTask,
        response: WebhookRestClient.WebhookResponse
    )
}
