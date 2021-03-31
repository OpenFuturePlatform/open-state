package io.openfuture.state.service

import io.openfuture.state.webhook.ScheduledTransaction
import java.time.LocalDateTime

interface WalletQueueService {

    suspend fun add(walletId: String, transaction: ScheduledTransaction)

    suspend fun remove(walletId: String)

    suspend fun walletsScheduledTo(timeStamp: LocalDateTime): List<String>

    suspend fun score(walletId: String): Double?

    suspend fun incrementScore(walletId: String, diff: Double)

    suspend fun lock(walletId: String): Boolean

    suspend fun unlock(walletId: String)
}
