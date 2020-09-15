package io.openfuture.state.blockchain.dto

import java.math.BigDecimal

data class UnifiedTransaction(
        val hash: String,
        val address: String,
        val amount: BigDecimal
)
