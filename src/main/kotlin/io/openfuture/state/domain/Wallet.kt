package io.openfuture.state.domain

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.LocalDateTime

@Document
@CompoundIndex(name = "address_blockchain_idx", def = "{'address': 1, 'blockchain': 1}")
data class Wallet(
        @Indexed val address: String,
        val webhook: String,
        val blockchain: String,
        var lastUpdate: LocalDateTime = LocalDateTime.now(),
        @MongoId val id: ObjectId = ObjectId(),
        private var transactions: List<Transaction> = emptyList(),
) {
    fun addTransaction(transaction: Transaction) {
        transactions = transactions.plus(transaction)
    }

    fun getTransactions(): List<Transaction> = transactions
}
