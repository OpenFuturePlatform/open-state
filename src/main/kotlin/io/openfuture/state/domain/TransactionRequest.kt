package io.openfuture.state.domain

import io.openfuture.state.model.BlockchainType
import org.web3j.protocol.core.methods.response.EthBlock
import kotlin.math.pow

class TransactionRequest(
        val walletAddress: String,
        val blockChainType: BlockchainType,
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
            walletAddress = walletAddress,
            blockChainType = BlockchainType.ETHEREUM,
            hash = tx.hash,
            from = tx.from,
            to = tx.to,
            amount = tx.value.toLong(),
            fee = tx.gas.toLong() * 10.0.pow(10.0).toLong(),
            date = blockTimeStamp,
            blockHeight = tx.blockNumber.toLong(),
            blockHash = tx.blockHash
    )
}
