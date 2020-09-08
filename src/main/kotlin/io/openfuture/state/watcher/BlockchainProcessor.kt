package io.openfuture.state.watcher

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.domain.AddTransactionRequest
import io.openfuture.state.repository.ProcessingRedisRepository
import io.openfuture.state.service.WalletService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BlockchainProcessor(
        private val walletService: WalletService,
        private val blockchains: List<Blockchain>,
        private val processingRedisRepository: ProcessingRedisRepository
) {

    @Scheduled(fixedDelayString = "\${watcher.process-delay}", initialDelay = 1000)
    fun process() = GlobalScope.launch {
        val blockchain = processingRedisRepository.pop()?.let { getBlockchainByName(it) } ?: return@launch
        val locked = processingRedisRepository.lockIfAbsent(blockchain)
        if (!locked) return@launch
        do {
            val last = processingRedisRepository.getLast(blockchain)
            val current = processingRedisRepository.getCurrent(blockchain)
            processBlock(blockchain.getBlock(current), blockchain)
            processingRedisRepository.incCurrent(blockchain)
            processingRedisRepository.lock(blockchain)
        } while (last > current)
        processingRedisRepository.unlock(blockchain)
    }

    private suspend fun processBlock(block: UnifiedBlock, blockchain: Blockchain) {
        val fromTransactions = block.transactions
                .filter { null != it.from }
                .filter { walletService.existsByBlockchainAndAddress(blockchain, it.from!!) }
                .map { AddTransactionRequest(it, it.from!!, block.date) }

        val toTransactions = block.transactions
                .filter { null != it.to }
                .filter { walletService.existsByBlockchainAndAddress(blockchain, it.to!!) }
                .map { AddTransactionRequest(it, it.to!!, block.date) }
        walletService.addTransactions(fromTransactions.plus(toTransactions))
    }

    private fun getBlockchainByName(name: String): Blockchain {
        return blockchains.find { it.getName() == name } ?: throw IllegalArgumentException("Can not find blockchain")
    }
}
