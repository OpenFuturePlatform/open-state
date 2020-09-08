package io.openfuture.state.domain

import io.openfuture.state.blockchain.dto.UnifiedTransaction
import java.time.LocalDateTime
import kotlin.math.pow

class AddTransactionRequest(
        val walletAddress: String,
        val hash: String,
        val from: String,
        val to: String,
        val amount: Long,
        val fee: Long,
        val date: LocalDateTime,
        val blockHeight: Long,
        val blockHash: String
) {
    constructor(tx: UnifiedTransaction, walletAddress: String, blockDateTime: LocalDateTime) : this(
            walletAddress,
            tx.hash,
            tx.from!!,
            tx.to!!,
            tx.amount,
            tx.gas * 10.0.pow(10.0).toLong(),
            blockDateTime,
            tx.blockHeight,
            tx.blockHash
    )
}
