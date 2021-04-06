package io.openfuture.state.domain

import io.openfuture.state.webhhok.WebhookRestClient
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

@Document
data class WebhookInvocation(
        @Indexed
        val walletIdentity: WalletIdentity,
        @Indexed
        val transactionId: String,
        private val invocations: MutableList<WebhookResult> = ArrayList(),
        @MongoId
        val id: String = ObjectId().toHexString()
) {

    fun addInvocation(webhookResult: WebhookResult) {
        invocations.add(webhookResult)
    }

    data class WebhookResult(
            val status: HttpStatus,
            val url: String,
            val attempt: Int,
            val message: String? = null,
            val timestamp: LocalDateTime = LocalDateTime.now()
    ) {

        constructor(response: WebhookRestClient.WebhookResponse, attempt: Int): this(
                response.status,
                response.url,
                attempt,
                response.message
        )
    }
}
