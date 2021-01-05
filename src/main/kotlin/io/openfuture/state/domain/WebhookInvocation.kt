package io.openfuture.state.domain

import io.openfuture.state.util.toEpochMilli
import io.openfuture.state.webhook.WebhookStatus
import org.bson.types.ObjectId
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.LocalDateTime

@Document
data class WebhookInvocation(
        val wallet: Wallet,
        val transaction: Transaction,
        val url: String,
        var attempts: Int = 0,
        var message: String? = null,
        @MongoId val id: ObjectId = ObjectId(),
        @LastModifiedDate var lastUpdate: LocalDateTime = LocalDateTime.now(),
        var status: WebhookStatus = WebhookStatus.NOT_INVOKED
) {
    fun address(): String = wallet.address

    fun score(): Double = transaction.date.toEpochMilli().toDouble()
}