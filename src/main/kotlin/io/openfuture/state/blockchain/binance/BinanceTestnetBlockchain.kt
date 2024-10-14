package io.openfuture.state.blockchain.binance

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.blockchain.dto.UnifiedTransaction
import io.openfuture.state.domain.CurrencyCode
import io.openfuture.state.util.toLocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.Utils
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
class BinanceTestnetBlockchain(@Qualifier("web3jBinanceTestnet") private val web3jBinanceTestnet: Web3j): Blockchain() {

    override suspend fun getLastBlockNumber(): Int = web3jBinanceTestnet.ethBlockNumber()
        .sendAsync().await()
        .blockNumber.toInt()

    override suspend fun getNonce(address: String): BigInteger = web3jBinanceTestnet.ethGetTransactionCount(address,
        DefaultBlockParameterName.LATEST
    ).send().transactionCount

    override suspend fun broadcastTransaction(signedTransaction: String): String {
        val result = web3jBinanceTestnet.ethSendRawTransaction(signedTransaction).send()

        if (result.hasError()) {
            throw Exception(result.error.message)
        }

        while (!web3jBinanceTestnet.ethGetTransactionReceipt(result.transactionHash).send().transactionReceipt.isPresent) {
            withContext(Dispatchers.IO) {
                Thread.sleep(1000)
            }
        }

        return web3jBinanceTestnet.ethGetTransactionReceipt(result.transactionHash).send().transactionReceipt.get().transactionHash
    }

    override suspend fun getTransactionStatus(transactionHash: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getBlock(blockNumber: Int): UnifiedBlock {
        val parameter = DefaultBlockParameterNumber(blockNumber.toLong())
        val block = web3jBinanceTestnet.ethGetBlockByNumber(parameter, true)
            .sendAsync().await()
            .block
        val transactions = obtainTransactions(block)
        val date = block.timestamp.toLong().toLocalDateTime()
        return UnifiedBlock(transactions, date, block.number.toLong(), block.hash)
    }

    override suspend fun getBalance(address: String): BigDecimal {
        val parameter = DefaultBlockParameterName.LATEST
        val balanceWei = web3jBinanceTestnet.ethGetBalance(address, parameter)
            .sendAsync().await()
            .balance
        //val contractInstance: ERC20 = ERC20.load(contractAddress, web3j, credentials, contractGasProvider)
        return Convert.fromWei(balanceWei.toString(), Convert.Unit.ETHER)
    }

    override suspend fun getContractBalance(address: String, contractAddress: String): BigDecimal {

        val functionBalance = org.web3j.abi.datatypes.Function(
            "balanceOf",
            listOf(Address(address)),
            listOf(object : TypeReference<Uint256>() {})
        )
        val encodedFunction = FunctionEncoder.encode(functionBalance)
        val ethCall: EthCall = web3jBinanceTestnet.ethCall(
            Transaction.createEthCallTransaction(address, contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).sendAsync().await()

        val value = ethCall.value
        val decode = FunctionReturnDecoder.decode(value, functionBalance.outputParameters)
        val contractBalance = BigInteger(value.substring(2, value.length), 16)
        decode.iterator().forEach { a -> println("a ${a.value} with ${a.typeAsString}") }

        println("Value $value")

        return Convert.fromWei(contractBalance.toString(), Convert.Unit.ETHER)
    }

    override suspend fun getCurrencyCode(): CurrencyCode {
        return CurrencyCode.BINANCE
    }

    override suspend fun getGasPrice(): BigInteger {
        return web3jBinanceTestnet.ethGasPrice().sendAsync().await().gasPrice
    }

    override suspend fun getGasLimit(): BigInteger {
        return web3jBinanceTestnet
            .ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false)
            .sendAsync().await()
            .block.gasLimit
    }

    private suspend fun obtainTransactions(ethBlock: EthBlock.Block): List<UnifiedTransaction> = ethBlock.transactions
        .map { it.get() as EthBlock.TransactionObject }
        .map { tx ->
            val to = tx.to ?: findContractAddress(tx.hash)
            val amount = Convert.fromWei(tx.value.toBigDecimal(), Convert.Unit.ETHER)
            UnifiedTransaction(tx.hash, tx.from, to, amount, true, to)
        }

    private suspend fun findContractAddress(transactionHash: String) = web3jBinanceTestnet.ethGetTransactionReceipt(transactionHash)
        .sendAsync().await()
        .transactionReceipt.get()
        .contractAddress

    companion object {
        private val DECODE_TYPES = Utils.convert(
            listOf(
                object : TypeReference<Address>(true) {},
                object : TypeReference<Uint256>() {}
            )
        )

        private const val TRANSFER_METHOD_SIGNATURE = "0xa9059cbb"
        private const val TRANSFER_INPUT_LENGTH = 138
    }
}