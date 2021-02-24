package io.openfuture.state.service

import io.openfuture.state.domain.WebhookExecution

interface WebhookExecutionService {

    suspend fun findByTransactionHash(hash: String): WebhookExecution?

    suspend fun save(webhookExecution: WebhookExecution): WebhookExecution
}
