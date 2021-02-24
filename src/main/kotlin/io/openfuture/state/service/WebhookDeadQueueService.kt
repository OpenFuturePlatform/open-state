package io.openfuture.state.service

import io.openfuture.state.domain.WebhookDeadQueue
import io.openfuture.state.webhook.ScheduledTransaction

interface WebhookDeadQueueService {

    suspend fun addTransactions(walletAddress: String, transactions: List<ScheduledTransaction>): WebhookDeadQueue

    suspend fun getTransactions(walletAddress: String): List<ScheduledTransaction>

    suspend fun hasTransactions(walletAddress: String): Boolean

    suspend fun remove(walletAddress: String)
}
