package io.openfuture.state.service

import io.openfuture.state.domain.WebhookDeadQueue
import io.openfuture.state.webhook.ScheduledTransaction

interface WebhookDeadQueueService {

    suspend fun addTransactions(walletKey: String, transactions: List<ScheduledTransaction>): WebhookDeadQueue

    suspend fun getTransactions(walletKey: String): List<ScheduledTransaction>

    suspend fun hasTransactions(walletKey: String): Boolean

    suspend fun remove(walletKey: String)
}
