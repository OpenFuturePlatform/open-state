package io.openfuture.state.service

import io.openfuture.state.domain.WebhookExecution

interface WebhookExecutionService {

    suspend fun findByTransactionId(id: String): WebhookExecution?

    suspend fun save(webhookExecution: WebhookExecution): WebhookExecution
}
