package io.openfuture.state.watcher

import io.openfuture.state.model.Blockchain

interface BlockchainProcessor {
    suspend fun getLastBlockNumber(): Long

    suspend fun processBlock(blockNumber: Long)

    suspend fun getBlockchain(): Blockchain
}
