package io.openfuture.state.domain

import java.time.LocalDateTime

data class Transaction(
        val hash: String,
        val participant: String,
        val amount: Long,
        val fee: Long,
        val date: LocalDateTime,
        val blockHeight: Long,
        val blockHash: String
)
