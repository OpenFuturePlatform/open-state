package io.openfuture.state.service

import io.openfuture.state.domain.TransactionQueueTask
import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WebhookInvocation
import io.openfuture.state.repository.WebhookInvocationRepository
import io.openfuture.state.webhook.WebhookRestClient
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Service

@Service
class DefaultWebhookInvocationService(
    private val repository: WebhookInvocationRepository
) : WebhookInvocationService {

    override suspend fun registerInvocation(
        wallet: Wallet,
        transactionTask: TransactionQueueTask,
        response: WebhookRestClient.WebhookResponse
    ) {
        val invocation = repository.findByTransactionId(transactionTask.transactionId).awaitFirstOrNull()
            ?: WebhookInvocation(wallet.identity, transactionTask.transactionId)

        invocation.addInvocation(WebhookInvocation.WebhookResult(response, transactionTask.attempt))
        repository.save(invocation)
    }

}
