package io.openfuture.state.domain

data class Transaction(
        val hash: String,
        val externalHash: String,
        val typeId: Int,
        val participant: String,
        val amount: Long,
        val fee: Long,
        val date: Long,
        val blockHeight: Long,
        val blockHash: String
)
