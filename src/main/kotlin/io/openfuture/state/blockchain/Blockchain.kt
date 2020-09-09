package io.openfuture.state.blockchain

import io.openfuture.state.blockchain.dto.UnifiedBlock

abstract class Blockchain {
    abstract suspend fun getLastBlockNumber(): Int

    abstract suspend fun getBlock(blockNumber: Int): UnifiedBlock

    fun getName(): String = javaClass.simpleName

    override fun toString(): String {
        return getName()
    }
}
