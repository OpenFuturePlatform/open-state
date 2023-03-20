package io.openfuture.state.blockchain

import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.blockchain.dto.UnifiedTransaction
import io.openfuture.state.domain.CurrencyCode
import io.openfuture.state.util.toLocalDateTime
import kotlinx.coroutines.future.await
import org.springframework.stereotype.Component
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.Utils
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterNumber
import org.web3j.protocol.core.methods.response.EthBlock
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