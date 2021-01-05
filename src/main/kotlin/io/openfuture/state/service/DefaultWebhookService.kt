package io.openfuture.state.service

import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WebhookInvocation
import io.openfuture.state.repository.WebhookInvocationRedisRepository
import io.openfuture.state.repository.WebhookInvocationRepository
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service

@Service
class DefaultWebhookService(
        private val repository: WebhookInvocationRepository,
        private val webhookRedisRepository: WebhookInvocationRedisRepository
) : WebhookService {

    override suspend fun queueWebhook(wallet: Wallet, transaction: Transaction): WebhookInvocation {
        val webhook = WebhookInvocation(
                wallet,
                transaction,
                wallet.webhook
        )

        webhookRedisRepository.add(webhook)
        return repository.save(webhook).awaitSingle()
    }
}
