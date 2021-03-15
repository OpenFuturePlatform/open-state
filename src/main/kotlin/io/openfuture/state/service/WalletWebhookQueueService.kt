package io.openfuture.state.service

import io.openfuture.state.webhook.ScheduledTransaction
import java.time.LocalDateTime

interface WalletWebhookQueueService {

    suspend fun add(walletKey: String, transaction: ScheduledTransaction)

    suspend fun remove(walletKey: String)

    suspend fun walletsScheduledTo(timeStamp: LocalDateTime): List<String>

    suspend fun score(walletKey: String): Double?

    suspend fun incrementScore(walletKey: String, diff: Double)

    suspend fun lock(walletKey: String): Boolean

    suspend fun unlock(walletKey: String)
}
