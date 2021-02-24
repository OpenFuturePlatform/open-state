package io.openfuture.state.domain

import io.openfuture.state.webhook.ScheduledTransaction
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.LocalDateTime

@Document
data class WebhookDeadQueue(
        val walletAddress: String,
        @Indexed private val transactions: List<ScheduledTransaction> = emptyList(),
        val timestamp: LocalDateTime = LocalDateTime.now(),
        @MongoId val id: ObjectId = ObjectId()
) {

    fun addTransactions(transactions: List<ScheduledTransaction>) {
        transactions.plus(transactions)
    }

    fun getTransactions(): List<ScheduledTransaction> = transactions
}
