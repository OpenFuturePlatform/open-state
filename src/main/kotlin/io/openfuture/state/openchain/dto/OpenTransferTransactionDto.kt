package io.openfuture.state.openchain.dto

import io.openfuture.state.openchain.entity.OpenTransferTransaction


data class OpenTransferTransactionDto(
        val id: Long,
        val hash: String,
        val amount: Long,
        val fee: Long,
        val date: Long,
        val blockHash: String,
        val senderAddress: String,
        val recipientAddress: String
) {

    constructor(tx: OpenTransferTransaction) : this(
            tx.id,
            tx.hash,
            tx.amount,
            tx.fee,
            tx.date,
            tx.blockHash,
            tx.senderAddress,
            tx.recipientAddress
    )

}