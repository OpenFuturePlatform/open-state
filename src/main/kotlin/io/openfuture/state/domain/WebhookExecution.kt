package io.openfuture.state.domain

import io.openfuture.state.webhook.WebhookResult
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId

@Document
data class WebhookExecution(
        @Indexed val walletAddress: String,
        val transactionHash: String?,
        private val invocations: List<WebhookResult> = emptyList(),
        @MongoId val id: ObjectId = ObjectId()
) {
    fun addInvocation(webhookResult: WebhookResult) {
        invocations.plus(webhookResult)
    }
}
