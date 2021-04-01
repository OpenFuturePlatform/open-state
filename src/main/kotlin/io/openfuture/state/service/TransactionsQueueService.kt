package io.openfuture.state.service

import io.openfuture.state.domain.TransactionQueueTask

interface TransactionsQueueService {

    suspend fun add(walletId: String, transaction: TransactionQueueTask)
}
