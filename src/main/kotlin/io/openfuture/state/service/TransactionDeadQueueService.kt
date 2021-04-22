package io.openfuture.state.service

import io.openfuture.state.domain.TransactionDeadQueue
import io.openfuture.state.domain.TransactionQueueTask
import io.openfuture.state.domain.WalletIdentity

interface TransactionDeadQueueService {

    suspend fun addTransactionToDeadQueue(walletIdentity: WalletIdentity, transactions: List<TransactionQueueTask>): TransactionDeadQueue

    suspend fun getTransactionFromDeadQueue(walletIdentity: WalletIdentity): List<TransactionQueueTask>

    suspend fun hasTransactions(walletIdentity: WalletIdentity): Boolean

    suspend fun removeFromDeadQueue(walletIdentity: WalletIdentity)
}
