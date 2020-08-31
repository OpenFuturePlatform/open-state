package io.openfuture.state.watcher.ethereum

import io.openfuture.state.domain.TransactionRequest
import io.openfuture.state.model.BlockchainType
import io.openfuture.state.repository.OffsetRepository
import io.openfuture.state.service.WalletService
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.methods.response.EthBlock
import java.math.BigInteger

@Component
class EthereumBlockProcessor(
        private val offsetRepository: OffsetRepository,
        private val walletService: WalletService,
        private val web3j: Web3j
) {

    suspend fun processNext() {
        val blockNumber = BigInteger.valueOf(offsetRepository.getCurrent(BlockchainType.ETHEREUM).awaitSingle())
        web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), true)
                .flowable()
                .asFlow()
                .filter { null != it.block }
                .collect { ethBlock ->
                    val transactions = obtainTransactions(ethBlock)
                    walletService.addTransactions(transactions)
                    offsetRepository.increment(BlockchainType.ETHEREUM).awaitSingle()
                }
    }

    private fun obtainTransactions(ethBlock: EthBlock): Set<TransactionRequest> {
        val transactionObjects = ethBlock
                .block
                .transactions
                .map { it.get() as EthBlock.TransactionObject }

        val fromTransactions = transactionObjects
                .filter { null != it.from }
                .filter { runBlocking { walletService.existsByAddress(it.from) } }
                .map { TransactionRequest(it, ethBlock.block.timestamp.toLong(), it.from) }
                .toSet()

        val toTransactions = transactionObjects
                .filter { null != it.to }
                .filter { runBlocking { walletService.existsByAddress(it.to) } }
                .map { TransactionRequest(it, ethBlock.block.timestamp.toLong(), it.to) }
                .toSet()
        return fromTransactions.union(toTransactions)
    }
}
