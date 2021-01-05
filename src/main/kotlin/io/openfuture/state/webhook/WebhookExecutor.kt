package io.openfuture.state.webhook

import io.openfuture.state.domain.WebhookInvocation

interface WebhookExecutor {

    suspend fun execute(webhookInvocation: WebhookInvocation)
}