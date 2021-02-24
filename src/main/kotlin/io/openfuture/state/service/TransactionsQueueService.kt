package io.openfuture.state.service

import io.openfuture.state.webhook.ScheduledTransaction

interface TransactionsQueueService {

    suspend fun addTransaction(walletAddress: String, transaction: ScheduledTransaction)

    suspend fun removeTransactions(walletAddress: String)

    suspend fun setAt(walletAddress: String, transaction: ScheduledTransaction, index: Long)

    suspend fun hasTransactions(walletAddress: String): Boolean

    suspend fun firstTransaction(walletAddress: String): ScheduledTransaction

    suspend fun findAll(walletAddress: String): List<ScheduledTransaction>
}
