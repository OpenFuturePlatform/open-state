package io.openfuture.state.blockchain.dto

import java.math.BigDecimal

data class UnifiedTransaction(
    val hash: String,
    val from: Set<String>,
    val to: String,
    val amount: BigDecimal
) {
    constructor(hash: String, from: String, to: String, amount: BigDecimal) : this(hash, setOf(from), to, amount)
}
