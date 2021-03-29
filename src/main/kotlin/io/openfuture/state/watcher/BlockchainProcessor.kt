package io.openfuture.state.watcher

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.repository.ProcessingRedisRepository
import io.openfuture.state.service.WalletService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Every given time interval gets a blockchain from queue(Redis)
 * then tries to lock processing of this blockchain
 * the idea is to be processed only by one at a time, as there may be more than one instances
 * if not locked it exits till next execution
 *
 * If successfully locked, processes up to the last known block
 * and at each iteration increases the life of the lock for this blockchain
 * After successful processing, releases lock for blockchain
 */
@Component
class BlockchainProcessor(
        private val walletService: WalletService,
        private val blockchains: List<Blockchain>,
        private val processingRepository: ProcessingRedisRepository
) {

    @Scheduled(fixedDelayString = "#{@processDelay}", initialDelay = 1000)
    fun process() = GlobalScope.launch {
        val blockchain = processingRepository.pop()?.let { findBlockchain(it) } ?: return@launch
        val locked = processingRepository.lockIfAbsent(blockchain)
        if (!locked) return@launch

        log.info("Start processing blocks from {}.", blockchain)

        do {
            val last = processingRepository.getLast(blockchain)
            val current = processingRepository.getCurrentOrElseLast(blockchain)
            log.info("Processing {} at {} from {}", blockchain, current, last)

            val block = blockchain.getBlock(current)
            walletService.addTransactions(blockchain, block)

            processingRepository.incCurrent(blockchain)
            processingRepository.lock(blockchain)
        } while (last > current)

        processingRepository.unlock(blockchain)
        log.info("{} has been processed.", blockchain)
    }

    private fun findBlockchain(name: String): Blockchain {
        return blockchains.find { it.getName() == name } ?: throw IllegalArgumentException("Can not find blockchain")
    }

    companion object {
        private val log = LoggerFactory.getLogger(BlockchainProcessor::class.java)
    }
}
