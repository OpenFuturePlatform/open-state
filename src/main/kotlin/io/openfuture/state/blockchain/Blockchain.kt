package io.openfuture.state.blockchain

import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.domain.CurrencyCode
import java.math.BigDecimal
import java.math.BigInteger

abstract class Blockchain {

    abstract suspend fun getLastBlockNumber(): Int
    abstract suspend fun getNonce(address: String): BigInteger
    abstract suspend fun broadcastTransaction(signedTransaction: String): String
    abstract suspend fun getTransactionStatus(transactionHash: String): Boolean
    abstract suspend fun getBlock(blockNumber: Int): UnifiedBlock
    abstract suspend fun getGasPrice(): BigInteger
    abstract suspend fun getGasLimit(): BigInteger
    abstract suspend fun getBalance(address: String): BigDecimal
    abstract suspend fun getContractBalance(address: String, contractAddress: String): BigDecimal
    open fun getName(): String = javaClass.simpleName
    abstract suspend fun getCurrencyCode(): CurrencyCode
    override fun toString(): String {
        return getName()
    }

}
