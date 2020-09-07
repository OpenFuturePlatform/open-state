package io.openfuture.state.watcher

import io.openfuture.state.repository.BlockchainRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component

@ConditionalOnProperty(name = ["watcher.enabled"])
@Component
class BlockchainWatcher(
        private val processors: List<BlockchainProcessor>,
        private val blockchainRepository: BlockchainRepository,
        private val threadPoolTaskExecutor: ThreadPoolTaskExecutor
) {

    @Scheduled(fixedDelayString = "\${watcher.processor-fixed-delay}", initialDelay = 1000)
    fun processBlocks() = GlobalScope.launch {
        processors.forEach { processor ->
            threadPoolTaskExecutor.execute {
                runBlocking {
                    val blockchain = processor.getBlockchain()
                    val lastBlockNumber = blockchainRepository.getLastBlockNumber(blockchain)
                    var currentBlockNumber = blockchainRepository.getCurrentBlockNumber(blockchain)
                    while (lastBlockNumber >= currentBlockNumber && blockchainRepository.lock(blockchain)) {
                        processor.processBlock(currentBlockNumber)
                        currentBlockNumber = blockchainRepository.incrementCurrentBlockNumber(blockchain)
                        blockchainRepository.unlock(blockchain)
                    }
                }
            }
        }
    }

    @Scheduled(fixedDelayString = "\${watcher.block-number-updater-fixed-delay}")
    fun updateLastBlockNumbers() = GlobalScope.launch {
        processors.forEach { processor ->
            threadPoolTaskExecutor.execute {
                runBlocking {
                    val blockchain = processor.getBlockchain()
                    val blockNumber = processor.getLastBlockNumber()
                    blockchainRepository.setLastBlockNumber(blockchain, blockNumber)
                }
            }
        }
    }
}
