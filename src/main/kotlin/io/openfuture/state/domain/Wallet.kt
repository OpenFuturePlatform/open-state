package io.openfuture.state.domain

import io.openfuture.state.model.Blockchain
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.LocalDateTime

@Document
data class Wallet(
        @Indexed(unique = true) val address: String,
        val webhook: String,
        val blockchain: Blockchain,
        var lastUpdate: LocalDateTime = LocalDateTime.now(),
        @MongoId val id: ObjectId = ObjectId(),
        private var transactions: List<Transaction> = emptyList(),
) {
    fun addTransaction(transaction: Transaction) {
        val temp = transactions.toMutableList()
        temp.add(transaction)
        transactions = temp
    }

    fun getTransactions(): List<Transaction> = transactions
}
