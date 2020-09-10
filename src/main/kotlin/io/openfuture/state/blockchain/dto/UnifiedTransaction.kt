package io.openfuture.state.blockchain.dto

import java.math.BigDecimal

data class UnifiedTransaction(
        val hash: String,
        val from: String,
        val to: String,
        val amount: BigDecimal
)
