package io.openfuture.state.service

import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.TransactionQueueTask
import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WalletQueueTask

interface WebhookService {

    suspend fun scheduleTransaction(wallet: Wallet, transaction: Transaction)

    suspend fun firstWalletInQueue(score: Double? = null): WalletQueueTask?

    suspend fun firstTransaction(walletId: String): TransactionQueueTask

    suspend fun lock(walletId: String): Boolean

    suspend fun unlock(walletId: String)

    suspend fun rescheduleWallet(wallet: Wallet)

    suspend fun rescheduleTransaction(wallet: Wallet, transactionTask: TransactionQueueTask)
}
