package io.openfuture.state.service

import io.openfuture.state.webhook.ScheduledTransaction

interface TransactionsQueueService {

    suspend fun add(walletKey: String, transaction: ScheduledTransaction)

    suspend fun remove(walletKey: String)

    suspend fun setAt(walletKey: String, transaction: ScheduledTransaction, index: Long)

    suspend fun hasTransactions(walletKey: String): Boolean

    suspend fun first(walletKey: String): ScheduledTransaction

    suspend fun findAll(walletKey: String): List<ScheduledTransaction>
}
