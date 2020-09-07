package io.openfuture.state.domain

import org.web3j.protocol.core.methods.response.EthBlock
import kotlin.math.pow

class AddTransactionRequest(
        val walletAddress: String,
        val hash: String,
        val from: String,
        val to: String,
        val amount: Long,
        val fee: Long,
        val date: Long,
        val blockHeight: Long,
        val blockHash: String
) {
    constructor(tx: EthBlock.TransactionObject, blockTimeStamp: Long, walletAddress: String) : this(
            walletAddress,
            tx.hash,
            tx.from,
            tx.to,
            tx.value.toLong(),
            tx.gas.toLong() * 10.0.pow(10.0).toLong(),
            blockTimeStamp,
            tx.blockNumber.toLong(),
            tx.blockHash
    )
}
