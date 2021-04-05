package io.openfuture.state.service

import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.TransactionQueueTask
import io.openfuture.state.domain.Wallet

interface WebhookService {

    suspend fun scheduleTransaction(wallet: Wallet, transaction: Transaction)

    suspend fun walletsScheduledForNow(): List<String>

    suspend fun firstTransaction(wallet: Wallet): TransactionQueueTask

    suspend fun lock(walletId: String): Boolean

    suspend fun unlock(walletId: String)
}
