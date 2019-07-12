package io.openfuture.state.domain.dto

class TransactionDto(
        val blockchainId: Long,
        val hash: String,
        val from: String,
        val to: String,
        val amount: Long,
        val date: Long,
        val blockHeight: Long,
        val blockHash: String

)
