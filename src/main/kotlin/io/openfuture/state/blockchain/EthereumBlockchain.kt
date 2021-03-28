package io.openfuture.state.blockchain

import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.blockchain.dto.UnifiedTransaction
import io.openfuture.state.util.toLocalDateTime
import kotlinx.coroutines.future.await
import org.springframework.stereotype.Component
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterNumber
import org.web3j.protocol.core.methods.response.EthBlock
import org.web3j.utils.Convert

@Component
class EthereumBlockchain(private val web3j: Web3j) : Blockchain() {

    override suspend fun getLastBlockNumber(): Int = web3j.ethBlockNumber()
            .sendAsync().await()
            .blockNumber.toInt()

    override suspend fun getBlock(blockNumber: Int): UnifiedBlock {
        val parameter = DefaultBlockParameterNumber(blockNumber.toLong())
        val block = web3j.ethGetBlockByNumber(parameter, true)
                .sendAsync().await()
                .block

        val transactions = obtainTransactions(block)
        val date = block.timestamp.toLong().toLocalDateTime()
        return UnifiedBlock(transactions, date, block.number.toLong(), block.hash)
    }

    private suspend fun obtainTransactions(ethBlock: EthBlock.Block): List<UnifiedTransaction> = ethBlock.transactions
            .map { it.get() as EthBlock.TransactionObject }
            .map { tx ->
                val to = tx.to ?: findContractAddress(tx.hash)
                val amount = Convert.fromWei(tx.value.toBigDecimal(), Convert.Unit.ETHER)
                UnifiedTransaction(tx.hash, setOf(tx.from), setOf(to), amount)
            }

    private suspend fun findContractAddress(transactionHash: String) = web3j.ethGetTransactionReceipt(transactionHash)
            .sendAsync().await()
            .transactionReceipt.get()
            .contractAddress

}
