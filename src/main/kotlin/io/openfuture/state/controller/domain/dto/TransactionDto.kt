package io.openfuture.state.controller.domain.dto

class TransactionDto(
        val blockchainId: Long,
        val hash: String,
        val from: String,
        val to: String,
        val amount: Long,
        val fee: Long,
        val date: Long,
        val blockHeight: Long,
        val blockHash: String
)
