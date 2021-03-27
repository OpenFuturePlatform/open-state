package io.openfuture.state.blockchain.bitcoin

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.blockchain.dto.UnifiedTransaction
import io.openfuture.state.util.toLocalDateTimeInSeconds
import org.springframework.stereotype.Component

@Component
class BitcoinBlockchain(private val bitcoinRpcClient: BitcoinRpcClient) : Blockchain() {

    override suspend fun getLastBlockNumber(): Int {
        val latestBlockHash = bitcoinRpcClient.getLatestBlockHash()
        return bitcoinRpcClient.getBlockHeight(latestBlockHash)
    }

    override suspend fun getBlock(blockNumber: Int): UnifiedBlock {
        val blockHash = bitcoinRpcClient.getBlockHash(blockNumber)
        val block = bitcoinRpcClient.getBlock(blockHash)

        return toUnifiedBlock(block)
    }

    private suspend fun toUnifiedBlock(btcBlock: BitcoinBlock): UnifiedBlock {
        return UnifiedBlock(
                toUnifiedTransactions(btcBlock.transactions),
                btcBlock.time.toLocalDateTimeInSeconds(),
                btcBlock.height,
                btcBlock.hash
        )
    }

    private suspend fun toUnifiedTransactions(btcTransactions: List<BitcoinTransaction>): List<UnifiedTransaction> {
        val transactions = btcTransactions.toMutableList()
        //delete coinbase transaction (miner award)
        transactions.removeAt(0)

        return transactions.flatMap { obtainTransactions(it) }
    }

    private suspend fun obtainTransactions(btcTransaction: BitcoinTransaction): List<UnifiedTransaction> {
        btcTransaction.inputs.toMutableList().removeAt(0)
        val inputAddresses = btcTransaction.inputs
                .toMutableList()
                .map { bitcoinRpcClient.getInputAddress(it.txId!!, it.outputNumber!!) }
                .toSet()

        //remove if its is `change address`
        btcTransaction.outputs.toMutableList().removeAll { it.addresses.any { address -> inputAddresses.contains(address) } }
        return btcTransaction.outputs
                .filter { it.addresses.isNotEmpty() }
                .map {
            UnifiedTransaction(
                    btcTransaction.hash,
                    inputAddresses,
                    it.addresses,
                    it.value
            )
        }
    }

}
