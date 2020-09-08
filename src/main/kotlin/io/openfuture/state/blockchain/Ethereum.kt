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
import java.math.BigInteger

@Component
class Ethereum(private val web3j: Web3j) : Blockchain() {

    override suspend fun getLastBlockNumber(): Long {
        return web3j.ethBlockNumber()
                .flowable()
                .asFlow()
                .first()
                .blockNumber
                .toLong()
    }

    override suspend fun getBlock(blockNumber: Long): UnifiedBlock {
        return web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber)), true)
                .flowable()
                .asFlow()
                .map { toUnifiedBlock(it) }
                .first()
    }

    private fun toUnifiedBlock(ethBlock: EthBlock): UnifiedBlock {
        return UnifiedBlock(obtainTransactions(ethBlock), ethBlock.block.timestamp.toLong().toLocalDateTime())
    }

    private fun obtainTransactions(ethBlock: EthBlock): List<UnifiedTransaction> {
        return ethBlock.block
                .transactions
                .map { UnifiedTransaction(it.get() as EthBlock.TransactionObject) }
    }
}
