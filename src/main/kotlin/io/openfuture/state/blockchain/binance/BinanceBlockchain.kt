package io.openfuture.state.blockchain.binance

import com.binance.dex.api.client.BinanceDexApiNodeClient
import com.binance.dex.api.client.domain.bridge.TransferOut
import com.binance.dex.api.client.domain.broadcast.HashTimerLockTransfer
import com.binance.dex.api.client.domain.broadcast.Transaction
import com.binance.dex.api.client.domain.broadcast.TxType
import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.blockchain.dto.UnifiedTransaction
import io.openfuture.state.util.toLocalDateTime
import org.springframework.stereotype.Component


@Component
class BinanceBlockchain(private val binanceClient: BinanceDexApiNodeClient) : Blockchain() {

    override suspend fun getLastBlockNumber(): Int {
        return binanceClient.nodeInfo.syncInfo.latestBlockHeight.toInt()
    }

    override suspend fun getBlock(blockNumber: Int): UnifiedBlock {
        val blockInfo = binanceClient.getBlockMetaByHeight(blockNumber.toLong())
        val transactions = binanceClient.getBlockTransactions(blockNumber.toLong())
        val unifiedTransactions = transactions.mapNotNull { mapByTransactionType(it) }

        return UnifiedBlock(
                unifiedTransactions,
                blockInfo.header.time.toLocalDateTime(),
                blockNumber.toLong(),
                blockInfo.header.dataHash
        )
    }

    private fun mapByTransactionType(tx: Transaction): UnifiedTransaction? {
        return when (tx.txType) {
            TxType.TRANSFER_OUT -> mapFromTransferOutTransaction(tx)
            TxType.HTL_TRANSFER -> mapFromHtlTransfer(tx)
            //TODO Implement transfer
            //TxType.TRANSFER -> mapFromTransfer(tx)
            else -> null
        }
    }

    private fun mapFromHtlTransfer(tx: Transaction): UnifiedTransaction {
        val htlTransfer = tx.realTx as HashTimerLockTransfer
        return UnifiedTransaction(
                tx.hash,
                htlTransfer.from,
                htlTransfer.to,
                htlTransfer.outAmount.sumOf { it.amount }.toBigDecimal()
        )
    }

    private fun mapFromTransferOutTransaction(tx: Transaction): UnifiedTransaction {
        val transferOut = tx.realTx as TransferOut
        return UnifiedTransaction(
                tx.hash,
                transferOut.from,
                transferOut.toAddress,
                transferOut.amount.amount.toBigDecimal()
        )
    }

}
