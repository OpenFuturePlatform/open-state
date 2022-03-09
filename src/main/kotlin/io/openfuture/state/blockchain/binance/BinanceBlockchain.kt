package io.openfuture.state.blockchain.binance

import com.binance.dex.api.client.BinanceDexApiNodeClient
import com.binance.dex.api.client.domain.TransferInfo
import com.binance.dex.api.client.domain.broadcast.Transaction
import com.binance.dex.api.client.domain.broadcast.TxType
import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.blockchain.dto.UnifiedTransaction
import io.openfuture.state.util.toLocalDateTime
import org.springframework.stereotype.Component


@Component
class BinanceBlockchain(private val client: BinanceDexApiNodeClient) : Blockchain() {

    override suspend fun getLastBlockNumber(): Int {
        return client.nodeInfo.syncInfo.latestBlockHeight.toInt()
    }

    override suspend fun getBlock(blockNumber: Int): UnifiedBlock {
        val blockInfo = client.getBlockMetaByHeight(blockNumber.toLong())
        val transactions = client.getBlockTransactions(blockNumber.toLong())
        val unifiedTransactions = transactions.mapNotNull { mapByTransactionType(it) }

        return UnifiedBlock(
            unifiedTransactions,
            blockInfo.header.time.toLocalDateTime(),
            blockNumber.toLong(),
            blockInfo.header.dataHash
        )
    }

    private fun mapByTransactionType(tx: Transaction): UnifiedTransaction? {
        return if (tx.txType == TxType.TRANSFER) mapFromTransferInfo(tx) else null
    }

    private fun mapFromTransferInfo(tx: Transaction): UnifiedTransaction? {
        val transferInfo = tx.realTx as TransferInfo
        val output = transferInfo.outputs.first()
        val coin = output.coins.firstOrNull { it.denom == "BNB" } ?: return null
        val inputAddresses = transferInfo.inputs.map { it.address }.toSet()
        return UnifiedTransaction(tx.hash, inputAddresses, output.address, coin.amount.toBigDecimal())
    }

}
