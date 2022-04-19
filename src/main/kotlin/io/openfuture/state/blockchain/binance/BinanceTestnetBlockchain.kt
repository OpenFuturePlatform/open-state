package io.openfuture.state.blockchain.binance

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.blockchain.dto.UnifiedTransaction
import io.openfuture.state.domain.CurrencyCode
import io.openfuture.state.property.SmartContractAddresses
import io.openfuture.state.util.toLocalDateTime
import kotlinx.coroutines.future.await
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.Utils
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterNumber
import org.web3j.protocol.core.methods.response.EthBlock
import java.math.BigDecimal

@Component
class BinanceTestnetBlockchain(
    @Qualifier("web3jBinanceTestnet") private val web3jBinanceTestnet: Web3j,
    private val smartContractAddresses: SmartContractAddresses
): Blockchain() {

    override suspend fun getLastBlockNumber(): Int = web3jBinanceTestnet.ethBlockNumber()
        .sendAsync().await()
        .blockNumber.toInt()

    override suspend fun getBlock(blockNumber: Int): UnifiedBlock {
        val parameter = DefaultBlockParameterNumber(blockNumber.toLong())
        val block = web3jBinanceTestnet.ethGetBlockByNumber(parameter, true)
            .sendAsync().await()
            .block
        val transactions = obtainTransactions(block)
        val date = block.timestamp.toLong().toLocalDateTime()
        return UnifiedBlock(transactions, date, block.number.toLong(), block.hash)
    }

    override suspend fun getCurrencyCode(): CurrencyCode {
        return CurrencyCode.BINANCE
    }

    private suspend fun obtainTransactions(ethBlock: EthBlock.Block): List<UnifiedTransaction> {
        val transactions = ethBlock
            .transactions
            ?.map { it.get() as EthBlock.TransactionObject }
            ?.filter { it.to != null } // drop smart contract creation transactions
            ?: throw IllegalStateException()

        val tokenTransfers = transactions
            .filter { isBep20Transfer(it) }
            .filter { smartContractAddresses.addresses.contains(it.to) }
            .map { mapBep20Transaction(it) }

        val nativeTransfers = transactions
            .filter { isNativeTransfer(it) }
            .map { UnifiedTransaction(it.hash, setOf(it.from), it.to, BigDecimal(it.value)) }

        listOf(tokenTransfers, nativeTransfers)
        return tokenTransfers + nativeTransfers
    }

    private fun isNativeTransfer(tx: EthBlock.TransactionObject): Boolean = tx.input == "0x"

    private fun isBep20Transfer(tx: EthBlock.TransactionObject): Boolean = tx.input.startsWith(TRANSFER_METHOD_SIGNATURE)
            && tx.input.length >= TRANSFER_INPUT_LENGTH

    private fun mapBep20Transaction(tx: EthBlock.TransactionObject): UnifiedTransaction {
        val result = FunctionReturnDecoder.decode(tx.input.drop(TRANSFER_METHOD_SIGNATURE.length), DECODE_TYPES)
        return UnifiedTransaction(
            tx.hash,
            setOf(result[0].value as String),
            tx.to,
            result[1].value as BigDecimal
        )
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