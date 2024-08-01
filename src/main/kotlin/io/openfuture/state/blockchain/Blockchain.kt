package io.openfuture.state.blockchain

import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.domain.CurrencyCode
import java.math.BigDecimal
import java.math.BigInteger

abstract class Blockchain {

    abstract suspend fun getLastBlockNumber(): Int

    abstract suspend fun getBlock(blockNumber: Int): UnifiedBlock

    abstract suspend fun getBalance(address: String): BigDecimal

    abstract suspend fun getContractBalance(address: String): BigDecimal

    open fun getName(): String = javaClass.simpleName

    abstract suspend fun getCurrencyCode(): CurrencyCode

    override fun toString(): String {
        return getName()
    }

}
