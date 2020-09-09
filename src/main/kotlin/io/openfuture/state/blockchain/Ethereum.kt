package io.openfuture.state.blockchain

import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.blockchain.dto.UnifiedTransaction
import io.openfuture.state.util.toLocalDateTime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import org.springframework.stereotype.Component
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.methods.response.EthBlock
import kotlin.math.pow

@Component
class Ethereum(private val web3j: Web3j) : Blockchain() {

    override suspend fun getLastBlockNumber(): Int {
        return web3j.ethBlockNumber()
                .flowable()
                .asFlow()
                .first()
                .blockNumber
                .toInt()
    }

    override suspend fun getBlock(blockNumber: Int): UnifiedBlock {
        return web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber.toBigInteger()), true)
                .flowable()
                .asFlow()
                .map {
                    val block = it.block
                    val transactions = obtainTransactions(it)
                    val date = block.timestamp.toLong().toLocalDateTime()
                    UnifiedBlock(transactions, date, block.number.toLong(), block.hash)
                }
                .first()
    }

    private fun obtainTransactions(ethBlock: EthBlock): List<UnifiedTransaction> {
        return ethBlock.block
                .transactions
                .map {
                    val tx = it.get() as EthBlock.TransactionObject
                    UnifiedTransaction(
                            tx.hash,
                            tx.from,
                            tx.to,
                            tx.value.toLong(),
                            tx.gas.toLong() * 10.0.pow(10.0).toLong(),
                    )
                }
    }
}
