package io.openfuture.state.service

import io.openfuture.state.domain.TransactionQueueTask

interface WalletQueueService {

    suspend fun add(walletId: String, transaction: TransactionQueueTask)

    suspend fun score(walletId: String): Double?
}
