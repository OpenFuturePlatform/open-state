package io.openfuture.state.service

import io.openfuture.state.domain.transaction.TransactionDeadQueue
import io.openfuture.state.domain.transaction.TransactionQueueTask
import io.openfuture.state.domain.wallet.WalletIdentity

interface TransactionDeadQueueService {

    suspend fun addTransactionToDeadQueue(walletIdentity: WalletIdentity, transactions: List<TransactionQueueTask>): TransactionDeadQueue

    suspend fun getTransactionFromDeadQueue(walletIdentity: WalletIdentity): List<TransactionQueueTask>

    suspend fun hasTransactions(walletIdentity: WalletIdentity): Boolean

    suspend fun removeFromDeadQueue(walletIdentity: WalletIdentity)
}
