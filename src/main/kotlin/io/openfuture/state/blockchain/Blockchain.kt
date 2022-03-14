package io.openfuture.state.blockchain

import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.domain.CurrencyCode

abstract class Blockchain {

    abstract suspend fun getLastBlockNumber(): Int

    abstract suspend fun getBlock(blockNumber: Int): UnifiedBlock

    open fun getName(): String = javaClass.simpleName

    abstract suspend fun getCurrencyCode(): CurrencyCode

    override fun toString(): String {
        return getName()
    }

}
