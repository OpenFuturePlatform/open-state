package io.openfuture.state.blockchain.bitcoin

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.bitcoin.dto.BitcoinBlock
import io.openfuture.state.blockchain.bitcoin.dto.BitcoinTransaction
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.blockchain.dto.UnifiedTransaction
import io.openfuture.state.util.toLocalDateTimeInSeconds
import org.springframework.stereotype.Component

@Component
class BitcoinBlockchain(private val client: BitcoinClient) : Blockchain() {

    override suspend fun getLastBlockNumber(): Int {
        val latestBlockHash = client.getLatestBlockHash()
        return client.getBlockHeight(latestBlockHash)
    }

    override suspend fun getBlock(blockNumber: Int): UnifiedBlock {
        val blockHash = client.getBlockHash(blockNumber)
        val block = client.getBlock(blockHash)

        return toUnifiedBlock(block)
    }

    private suspend fun toUnifiedBlock(block: BitcoinBlock): UnifiedBlock = UnifiedBlock(
        toUnifiedTransactions(block.transactions),
        block.time.toLocalDateTimeInSeconds(),
        block.height,
        block.hash
    )

    private suspend fun toUnifiedTransactions(transactions: List<BitcoinTransaction>): List<UnifiedTransaction> {
        return transactions
            .drop(1) // skip coinbase transaction (miner award)
            .flatMap { obtainTransactions(it) }
    }

    private suspend fun obtainTransactions(transaction: BitcoinTransaction): List<UnifiedTransaction> {
        val inputAddresses = getInputAddresses(transaction.inputs)

        return transaction.outputs
            .filter { it.addresses.isNotEmpty() }
            .filter { !containsChangeAddresses(inputAddresses, it.addresses) }
            .map { UnifiedTransaction(transaction.hash, inputAddresses, it.addresses.first(), it.value) }
    }

    private fun containsChangeAddresses(inputAddresses: Set<String>, outputAddresses: Set<String>): Boolean {
        return outputAddresses.any { address -> inputAddresses.contains(address) }
    }

    private suspend fun getInputAddresses(inputs: List<BitcoinTransaction.Input>): Set<String> {
        return inputs
            .flatMap { client.getInputAddresses(it.txId!!, it.outputNumber!!) }
            .toSet()
    }

}
