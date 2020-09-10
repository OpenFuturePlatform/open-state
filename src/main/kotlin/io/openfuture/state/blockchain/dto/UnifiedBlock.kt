package io.openfuture.state.blockchain.dto

import java.time.LocalDateTime

data class UnifiedBlock(
        val transactions: List<UnifiedTransaction>,
        val date: LocalDateTime,
        val number: Long,
        val hash: String
)
