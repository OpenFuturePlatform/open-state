package io.openfuture.state.domain.dto

import java.time.LocalDateTime

class TransactionDto(
        val blockchainId: Long,
        val hash: String,
        val from: String,
        val to: String,
        val amount: Long,
        val date: LocalDateTime,
        val blockHeight: Long,
        val blockHash: String

)
