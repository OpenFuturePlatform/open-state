package io.openfuture.state.blockchain.ethereum

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.blockchain.dto.UnifiedTransaction
import io.openfuture.state.domain.CurrencyCode
import io.openfuture.state.util.toLocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.protocol.core.DefaultBlockParameterName.LATEST
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.DefaultBlockParameterNumber
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthBlock
import org.web3j.protocol.core.methods.response.EthCall
import org.web3j.utils.Convert
import java.lang.Exception
import java.math.BigDecimal
import java.math.BigInteger

@Component
@ConditionalOnProperty(value = ["production.mode.enabled"], havingValue = "true")
class EthereumBlockchain(private val web3j: Web3j) : Blockchain() {

    override suspend fun getLastBlockNumber(): Int = web3j.ethBlockNumber()
        .sendAsync().await()
        .blockNumber.toInt()

    override suspend fun getNonce(address: String): BigInteger = web3j.ethGetTransactionCount(address, LATEST).send().transactionCount
    override suspend fun broadcastTransaction(signedTransaction: String): String {
        val result = web3j.ethSendRawTransaction(signedTransaction).send()

        if (result.hasError()) {
            throw Exception(result.error.message)
        }

        while (!web3j.ethGetTransactionReceipt(result.transactionHash).send().transactionReceipt.isPresent) {
            withContext(Dispatchers.IO) {
                Thread.sleep(1000)
            }
        }

        return web3j.ethGetTransactionReceipt(result.transactionHash).send().transactionReceipt.get().transactionHash
    }

    override suspend fun getTransactionStatus(transactionHash: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getBlock(blockNumber: Int): UnifiedBlock {
        val parameter = DefaultBlockParameterNumber(blockNumber.toLong())
        val block = web3j.ethGetBlockByNumber(parameter, true)
            .sendAsync().await()
            .block
        val transactions = obtainTransactions(block)
        val date = block.timestamp.toLong().toLocalDateTime()
        return UnifiedBlock(transactions, date, block.number.toLong(), block.hash)
    }

    override suspend fun getBalance(address: String): BigDecimal {
        val balanceWei = web3j.ethGetBalance(address, LATEST)
            .sendAsync().await()
            .balance
        return Convert.fromWei(balanceWei.toString(), Convert.Unit.ETHER)
    }

    override suspend fun getContractBalance(address: String, contractAddress: String): BigDecimal {

        val functionBalance = org.web3j.abi.datatypes.Function(
            "balanceOf",
            listOf(Address(address)),
            listOf(object : TypeReference<Uint256>() {})
        )
        val encodedFunction = FunctionEncoder.encode(functionBalance)
        val ethCall: EthCall = web3j.ethCall(
            Transaction.createEthCallTransaction(address, contractAddress, encodedFunction),
            LATEST
        ).sendAsync().await()

        val value = ethCall.value
        val contractBalance = BigInteger(value.substring(2, value.length), 16)

        return Convert.fromWei(contractBalance.toString(), Convert.Unit.ETHER)
    }

    override suspend fun getGasPrice(): BigInteger {
        return web3j.ethGasPrice().sendAsync().await().gasPrice
    }

    override suspend fun getGasLimit(): BigInteger {
        return web3j
            .ethGetBlockByNumber(LATEST, false)
            .sendAsync().await()
            .block.gasLimit
    }

    override suspend fun getCurrencyCode(): CurrencyCode {
        return CurrencyCode.ETHEREUM
    }

    private suspend fun obtainTransactions(ethBlock: EthBlock.Block): List<UnifiedTransaction> = ethBlock.transactions
        .map { it.get() as EthBlock.TransactionObject }
        .map { tx ->
            val to = tx.to ?: findContractAddress(tx.hash)
            val amount = Convert.fromWei(tx.value.toBigDecimal(), Convert.Unit.ETHER)
            UnifiedTransaction(tx.hash, tx.from, to, amount, true, to)
        }

    private suspend fun findContractAddress(transactionHash: String) = web3j.ethGetTransactionReceipt(transactionHash)
        .sendAsync().await()
        .transactionReceipt.get()
        .contractAddress

}
