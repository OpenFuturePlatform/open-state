package io.openfuture.state.domain

import io.openfuture.state.webhook.WebhookStatus
import org.bson.types.ObjectId
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.LocalDateTime

@Document
@CompoundIndex(name = "address_blockchain_idx", def = "{'address': 1, 'blockchain': 1}")
data class Wallet(
        val blockchain: String,
        @Indexed val address: String,
        var webhook: String,
        var webhookStatus: WebhookStatus = WebhookStatus.NOT_INVOKED,
        @DBRef private var transactions: List<Transaction> = emptyList(),
        @LastModifiedDate var lastUpdate: LocalDateTime = LocalDateTime.now(),
        @MongoId val id: ObjectId = ObjectId(),
) {
    fun addTransaction(transaction: Transaction) {
        transactions = transactions.plus(transaction)
    }

    fun getTransactions(): List<Transaction> = transactions

    fun walletKey(): String = "[$blockchain] - [$address]"
}
