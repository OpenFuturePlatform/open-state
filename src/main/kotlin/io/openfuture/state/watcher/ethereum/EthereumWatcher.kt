package io.openfuture.state.watcher.ethereum

import io.openfuture.state.domain.AddTransactionRequest
import io.openfuture.state.model.Blockchain
import io.openfuture.state.repository.OffsetRepository
import io.openfuture.state.service.WalletService
import io.openfuture.state.watcher.BlockProcessor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.methods.response.EthBlock
import java.math.BigInteger

@Component
@ConditionalOnProperty(name = ["watcher.ethereum.enabled"])
class EthereumWatcher(
        private val offsetRepository: OffsetRepository,
        private val walletService: WalletService,
        private val web3j: Web3j
) : BlockProcessor {

    @Scheduled(fixedDelayString = "\${watcher.ethereum.fixed-delay}")
    fun start() = runBlocking {
        processNext()
    }

    override suspend fun processNext() {
        val blockNumber = BigInteger.valueOf(offsetRepository.getCurrent(Blockchain.ETHEREUM).awaitSingle())
        web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), true)
                .flowable()
                .asFlow()
                .filter { null != it.block }
                .collect { ethBlock ->
                    val transactions = obtainTransactions(ethBlock)
                    walletService.addTransactions(transactions)
                    offsetRepository.increment(Blockchain.ETHEREUM).awaitSingle()
                }
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
