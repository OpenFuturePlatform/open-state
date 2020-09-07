package io.openfuture.state.watcher.ethereum

import io.openfuture.state.domain.AddTransactionRequest
import io.openfuture.state.model.Blockchain
import io.openfuture.state.service.WalletService
import io.openfuture.state.watcher.BlockchainProcessor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.methods.response.EthBlock
import java.math.BigInteger

@Component
class EthereumBlockchainProcessor(
        private val walletService: WalletService,
        private val web3j: Web3j
) : BlockchainProcessor {

    override suspend fun getLastBlockNumber(): Long {
        return web3j.ethBlockNumber()
                .flowable()
                .asFlow()
                .first()
                .blockNumber
                .toLong()
    }

    override suspend fun processBlock(blockNumber: Long) {
        web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber)), true)
                .flowable()
                .asFlow()
                .filter { null != it.block }
                .collect { ethBlock ->
                    val transactions = obtainTransactions(ethBlock)
                    walletService.addTransactions(transactions)
                }
    }

    override suspend fun getBlockchain(): Blockchain {
        return Blockchain.ETHEREUM
    }

    private fun obtainTransactions(ethBlock: EthBlock): List<AddTransactionRequest> {
        val transactionObjects = ethBlock
                .block
                .transactions
                .map { it.get() as EthBlock.TransactionObject }

        val fromTransactions = transactionObjects
                .filter { null != it.from }
                .filter { runBlocking { walletService.existsByAddressAndBlockchain(it.from, Blockchain.ETHEREUM) } }
                .map { AddTransactionRequest(it, ethBlock.block.timestamp.toLong(), it.from) }

        val toTransactions = transactionObjects
                .filter { null != it.to }
                .filter { runBlocking { walletService.existsByAddressAndBlockchain(it.to, Blockchain.ETHEREUM) } }
                .map { AddTransactionRequest(it, ethBlock.block.timestamp.toLong(), it.to) }
        return fromTransactions + toTransactions
    }
}
