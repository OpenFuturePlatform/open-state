package io.openfuture.state.domain

import java.time.LocalDateTime

data class TransactionQueueTask(
    val transactionId: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
        val transactionId: String = "",
        var attempt:Int = 1,
        var timestamp: LocalDateTime = LocalDateTime.now()
)
