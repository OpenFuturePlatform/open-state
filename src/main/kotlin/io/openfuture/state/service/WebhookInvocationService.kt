package io.openfuture.state.service

import io.openfuture.state.domain.TransactionQueueTask
import io.openfuture.state.domain.Wallet
import io.openfuture.state.webhhok.WebhookRestClient

interface WebhookInvocationService {

    suspend fun registerInvocation(wallet: Wallet, transactionTask: TransactionQueueTask, response: WebhookRestClient.WebhookResponse)
}
