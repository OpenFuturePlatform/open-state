package io.openfuture.state.domain

import io.openfuture.state.model.BlockchainType

data class Transaction(
        val blockchainType: BlockchainType,
        val hash: String,
        val participant: String,
        val amount: Long,
        val fee: Long,
        val date: Long,
        val blockHeight: Long,
        val blockHash: String
)
