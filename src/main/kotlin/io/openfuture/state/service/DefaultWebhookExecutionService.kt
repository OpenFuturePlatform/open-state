package io.openfuture.state.service

import io.openfuture.state.domain.WebhookExecution
import io.openfuture.state.repository.WebhookExecutionRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service

@Service
class DefaultWebhookExecutionService(
        private val repository: WebhookExecutionRepository
): WebhookExecutionService  {

    override suspend fun findByTransactionId(id: String): WebhookExecution? {
        return repository.findByTransactionId(id).awaitFirstOrNull()
    }

    override suspend fun save(webhookExecution: WebhookExecution): WebhookExecution {
        return repository.save(webhookExecution).awaitSingle()
    }
}
