package io.openfuture.state.domain

import java.math.BigDecimal
import java.time.LocalDateTime

data class Transaction(
        val hash: String,
        val from: Set<String>,
        val to: Set<String>,
        val amount: BigDecimal,
        val date: LocalDateTime,
        val blockHeight: Long,
        val blockHash: String
)
