package io.openfuture.state.webhook.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class TransactionPayload(
    val hash: String,
    val from: String,
    val to: String,
    val amount: BigDecimal,
    val date: LocalDateTime,
    val blockHeight: Long,
    val blockHash: String
)
