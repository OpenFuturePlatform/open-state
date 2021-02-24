package io.openfuture.state.service

import io.openfuture.state.webhook.ScheduledTransaction
import java.time.LocalDateTime

interface WalletWebhookQueueService {

    suspend fun add(walletAddress: String, transaction: ScheduledTransaction)

    suspend fun remove(walletAddress: String)

    suspend fun walletsScheduledTo(timeStamp: LocalDateTime): List<String>

    suspend fun score(walletAddress: String): Double?

    suspend fun incrementScore(walletAddress: String, diff: Double)

    suspend fun lock(walletAddress: String): Boolean

    suspend fun unlock(walletAddress: String)
}
