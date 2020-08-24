package io.openfuture.state.controller.domain.dto

import io.openfuture.state.entity.Transaction

data class TrackingTransactionDto(
        val id: String,
        val hash: String,
        val amount: Long,
        val fee: Long,
        val address: String,
        val type: String,
        val participant: String,
        val date: Long,
        val externalHash: String,
        val blockHeight: Long,
        val blockHash: String

) {

    constructor(tx: Transaction) : this(
            tx.id.toString(),
            tx.hash,
            tx.amount,
            tx.fee,
            tx.walletAddress,
            tx.getType().name,
            tx.participant,
            tx.date,
            tx.externalHash,
            tx.blockHeight,
            tx.blockHash
    )

}
