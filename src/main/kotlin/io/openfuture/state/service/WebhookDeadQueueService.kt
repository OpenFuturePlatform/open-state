package io.openfuture.state.service

import io.openfuture.state.domain.WalletAddress
import io.openfuture.state.domain.WebhookDeadQueue
import io.openfuture.state.webhook.ScheduledTransaction

interface WebhookDeadQueueService {

    suspend fun addTransactions(address: WalletAddress, transactions: List<ScheduledTransaction>): WebhookDeadQueue

    suspend fun getTransactions(address: WalletAddress): List<ScheduledTransaction>

    suspend fun hasTransactions(address: WalletAddress): Boolean

    suspend fun remove(address: WalletAddress)
}
