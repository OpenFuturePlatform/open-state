package io.openfuture.state.blockchain.dto

import org.web3j.protocol.core.methods.response.EthBlock

data class UnifiedTransaction(
        val hash: String,
        val from: String?,
        val to: String?,
        val amount: Long,
        val gas: Long,
        val blockHeight: Long,
        val blockHash: String
) {
    constructor(tx: EthBlock.TransactionObject) : this(
            tx.hash,
            tx.from,
            tx.to,
            tx.value.toLong(),
            tx.gas.toLong(),
            tx.blockNumber.toLong(),
            tx.blockHash
    )
}
