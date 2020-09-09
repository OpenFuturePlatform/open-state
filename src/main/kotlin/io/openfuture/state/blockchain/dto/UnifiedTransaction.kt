package io.openfuture.state.blockchain.dto

data class UnifiedTransaction(
        val hash: String,
        val from: String,
        val to: String,
        val amount: Long,
        val fee: Long
)
