package io.openfuture.state.domain

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.LocalDateTime
import java.util.*

@Document
data class TransactionDeadQueue(
    @Indexed
    val walletIdentity: WalletIdentity,
    @Indexed
    private val transactions: MutableList<TransactionQueueTask> = LinkedList(),
    val timestamp: LocalDateTime = LocalDateTime.now(),
    @MongoId
    val id: String = ObjectId().toHexString()
) {

    fun addTransactions(transactionTasks: List<TransactionQueueTask>) {
        transactions.addAll(transactionTasks)
    }

    fun getTransactions(): List<TransactionQueueTask> = transactions

}
