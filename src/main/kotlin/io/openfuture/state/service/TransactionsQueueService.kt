package io.openfuture.state.service

import io.openfuture.state.domain.TransactionQueueTask

interface TransactionsQueueService {

    suspend fun add(walletId: String, transaction: TransactionQueueTask)

    suspend fun first(walletId: String): TransactionQueueTask

    suspend fun remove(walletId: String)

    suspend fun setAt(walletId: String, transaction: TransactionQueueTask, index: Long)

    suspend fun hasTransactions(walletId: String): Boolean
}
