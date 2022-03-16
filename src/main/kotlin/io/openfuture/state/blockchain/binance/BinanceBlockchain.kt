package io.openfuture.state.blockchain.binance

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.blockchain.dto.UnifiedTransaction
import io.openfuture.state.domain.CurrencyCode
import io.openfuture.state.util.toLocalDateTime
import kotlinx.coroutines.future.await
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterNumber
import org.web3j.protocol.core.methods.response.EthBlock
import org.web3j.utils.Convert


@Component
@ConditionalOnProperty(value = ["production.mode.enabled"], havingValue = "true")
class BinanceBlockchain(@Qualifier("web3jBinance") private val web3jBinance: Web3j) : Blockchain() {

    override suspend fun getLastBlockNumber(): Int = web3jBinance.ethBlockNumber()
        .sendAsync().await()
        .blockNumber.toInt()

    override suspend fun getBlock(blockNumber: Int): UnifiedBlock {
        val parameter = DefaultBlockParameterNumber(blockNumber.toLong())
        val block = web3jBinance.ethGetBlockByNumber(parameter, true)
            .sendAsync().await()
            .block
        val transactions = obtainTransactions(block)
        val date = block.timestamp.toLong().toLocalDateTime()
        return UnifiedBlock(transactions, date, block.number.toLong(), block.hash)
    }

    override suspend fun getCurrencyCode(): CurrencyCode {
        return CurrencyCode.BINANCE
    }

    private suspend fun obtainTransactions(ethBlock: EthBlock.Block): List<UnifiedTransaction> = ethBlock.transactions
        .map { it.get() as EthBlock.TransactionObject }
        .map { tx ->
            val to = tx.to ?: findContractAddress(tx.hash)
            val amount = Convert.fromWei(tx.value.toBigDecimal(), Convert.Unit.ETHER)
            UnifiedTransaction(tx.hash, tx.from, to, amount)
        }

    private suspend fun findContractAddress(transactionHash: String) = web3jBinance.ethGetTransactionReceipt(transactionHash)
        .sendAsync().await()
        .transactionReceipt.get()
        .contractAddress

}
