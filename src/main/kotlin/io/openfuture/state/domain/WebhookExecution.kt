package io.openfuture.state.domain

import io.openfuture.state.webhook.WebhookResult
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId

@Document
data class WebhookExecution(
        @Indexed
        val walletAddress: WalletAddress,
        @Indexed
        val transactionId: String,
        private val invocations: List<WebhookResult> = emptyList(),
        @MongoId
        val id: String = ObjectId().toHexString()
) {

    constructor(transaction: Transaction): this(
            transaction.walletAddress,
            transaction.id
    )

    fun addInvocation(webhookResult: WebhookResult) {
        invocations.plus(webhookResult)
    }
}
