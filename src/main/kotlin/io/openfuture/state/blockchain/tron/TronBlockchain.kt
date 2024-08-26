package io.openfuture.state.blockchain.tron

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.blockchain.dto.UnifiedTransaction
import io.openfuture.state.domain.CurrencyCode
import io.openfuture.state.util.toLocalDateTime
import kotlinx.coroutines.future.await
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
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
@ConditionalOnProperty(value = ["production.mode.enabled"], havingValue = "true")
class TronBlockchain(@Qualifier("web3jTronTestnet") private val web3jTronTestnet: Web3j) : Blockchain() {

    override suspend fun getLastBlockNumber(): Int = web3jTronTestnet.ethBlockNumber()
        .sendAsync().await()
        .blockNumber.toInt()

    override suspend fun getBlock(blockNumber: Int): UnifiedBlock {
        val parameter = DefaultBlockParameterNumber(blockNumber.toLong())
        val block = web3jTronTestnet.ethGetBlockByNumber(parameter, true)
            .sendAsync().await()
            .block
        val transactions = obtainTransactions(block)
        val date = block.timestamp.toLong().toLocalDateTime()
        return UnifiedBlock(transactions, date, block.number.toLong(), block.hash)
    }

    override suspend fun getBalance(address: String): BigDecimal {
        val parameter = DefaultBlockParameterName.LATEST
        val balanceWei = web3jTronTestnet.ethGetBalance(address, parameter)
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
        val ethCall: EthCall = web3jTronTestnet.ethCall(
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
        return CurrencyCode.TRON
    }

    private suspend fun obtainTransactions(ethBlock: EthBlock.Block): List<UnifiedTransaction> = ethBlock.transactions
        .map { it.get() as EthBlock.TransactionObject }
        .map { tx ->
            val to = tx.to ?: findContractAddress(tx.hash)
            val amount = Convert.fromWei(tx.value.toBigDecimal(), Convert.Unit.ETHER)
            UnifiedTransaction(tx.hash, tx.from, to, amount, true, to)
        }

    private suspend fun findContractAddress(transactionHash: String) =
        web3jTronTestnet.ethGetTransactionReceipt(transactionHash)
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
