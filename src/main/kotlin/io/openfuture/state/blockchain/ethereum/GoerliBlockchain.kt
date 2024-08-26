package io.openfuture.state.blockchain.ethereum

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.blockchain.dto.UnifiedTransaction
import io.openfuture.state.domain.CurrencyCode
import io.openfuture.state.util.toLocalDateTime
import kotlinx.coroutines.future.await
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
import java.math.BigDecimal
import java.math.BigInteger

@Component
class GoerliBlockchain(private val web3jTest: Web3j): Blockchain() {

    override suspend fun getLastBlockNumber(): Int = web3jTest.ethBlockNumber()
        .sendAsync().await()
        .blockNumber.toInt()

    override suspend fun getBlock(blockNumber: Int): UnifiedBlock {
        val parameter = DefaultBlockParameterNumber(blockNumber.toLong())
        val block = web3jTest.ethGetBlockByNumber(parameter, true)
            .sendAsync().await()
            .block
        val transactions = obtainTransactions(block)

        val date = block.timestamp.toLong().toLocalDateTime()
        return UnifiedBlock(transactions, date, block.number.toLong(), block.hash)
    }

    override suspend fun getBalance(address: String): BigDecimal {
        val parameter = DefaultBlockParameterName.LATEST
        val balanceWei = web3jTest.ethGetBalance(address, parameter)
            .sendAsync().await()
            .balance
        return Convert.fromWei(balanceWei.toString(), Convert.Unit.ETHER)
    }

    override suspend fun getContractBalance(address: String, contractAddress: String): BigDecimal {

        val functionBalance: org.web3j.abi.datatypes.Function = org.web3j.abi.datatypes.Function(
            "balanceOf",
            listOf(Address(address)),
            listOf(object : TypeReference<Uint256>() {})
        )
        val encodedFunction = FunctionEncoder.encode(functionBalance)
        val ethCall: EthCall = web3jTest.ethCall(
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
        return CurrencyCode.ETHEREUM
    }

    private suspend fun obtainTransactions(ethBlock: EthBlock.Block): List<UnifiedTransaction> {
        val transactions = ethBlock.transactions
            .map { it.get() as EthBlock.TransactionObject }
            .filter { it.to != null }

        val tokenTransfers = transactions
            .filter { isErc20Transfer(it) }
            .map { mapErc20Transaction(it) }

        val nativeTransfers = transactions
            .filter { isNativeTransfer(it) }
            .map { UnifiedTransaction(it.hash, it.from, it.to, Convert.fromWei(it.value.toBigDecimal(), Convert.Unit.ETHER), true, it.from) }

        return tokenTransfers + nativeTransfers
    }

    private fun isNativeTransfer(tx: EthBlock.TransactionObject): Boolean = tx.input == "0x"

    private fun isErc20Transfer(tx: EthBlock.TransactionObject): Boolean = tx.input.startsWith(TRANSFER_METHOD_SIGNATURE)
            && tx.input.length >= TRANSFER_INPUT_LENGTH

    private suspend fun mapErc20Transaction(tx: EthBlock.TransactionObject): UnifiedTransaction {
        val result = FunctionReturnDecoder.decode(tx.input.drop(TRANSFER_METHOD_SIGNATURE.length), DECODE_TYPES)
        val contractAddress = findContractAddress(tx.hash)

        return UnifiedTransaction(
            tx.hash,
            tx.from,
            result[0].value as String,
            BigDecimal(result[1].value as BigInteger),
            false,
            contractAddress
        )
    }

    private suspend fun findContractAddress(transactionHash: String): String{
        val transactionReceipt = web3jTest.ethGetTransactionReceipt(transactionHash)
            .sendAsync().await()
            .transactionReceipt

        var address = ""
        transactionReceipt.get().logs.forEach{
            address = it.address
        }

        return address
    }

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