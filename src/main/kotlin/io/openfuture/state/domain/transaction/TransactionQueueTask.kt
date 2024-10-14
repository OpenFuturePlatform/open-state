package io.openfuture.state.domain.transaction

import java.time.LocalDateTime

data class TransactionQueueTask(
    val transactionId: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    var attempt: Int = 1
)
