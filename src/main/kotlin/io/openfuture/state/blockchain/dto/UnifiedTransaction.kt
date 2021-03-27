package io.openfuture.state.blockchain.dto

import java.math.BigDecimal

data class UnifiedTransaction(
        val hash: String,
        val from: Set<String>,
        val to: Set<String>,
        val amount: BigDecimal
)
