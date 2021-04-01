package io.openfuture.state.service

import io.openfuture.state.domain.TransactionQueueTask
import java.time.LocalDateTime

interface WalletQueueService {

    suspend fun add(walletId: String, transaction: TransactionQueueTask)

    suspend fun score(walletId: String): Double?

    suspend fun walletsScheduledTo(timeStamp: LocalDateTime): List<String>

    suspend fun lock(walletId: String): Boolean

    suspend fun unlock(walletId: String)
}
