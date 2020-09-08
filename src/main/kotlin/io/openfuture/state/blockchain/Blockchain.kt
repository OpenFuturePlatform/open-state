package io.openfuture.state.blockchain

import io.openfuture.state.blockchain.dto.UnifiedBlock

abstract class Blockchain {
    abstract suspend fun getLastBlockNumber(): Long

    abstract suspend fun getBlock(blockNumber: Long): UnifiedBlock

    fun getName(): String = javaClass.simpleName

    override fun toString(): String {
        return getName()
    }
}
