package io.openfuture.state.service

import io.openfuture.state.webhook.ScheduledTransaction

interface TransactionsQueueService {

    suspend fun add(walletId: String, transaction: ScheduledTransaction)

    suspend fun remove(walletId: String)

    suspend fun setAt(walletId: String, transaction: ScheduledTransaction, index: Long)

    suspend fun hasTransactions(walletId: String): Boolean

    suspend fun first(walletId: String): ScheduledTransaction

    suspend fun findAll(walletId: String): List<ScheduledTransaction>
}
