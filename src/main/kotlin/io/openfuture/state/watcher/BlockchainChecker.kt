package io.openfuture.state.watcher

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.repository.ProcessingRedisRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Checks every given time whether a new block arrived in blockchains each
 * if yes then it puts new block's number to Redis as the last one
 * Also puts to queue blockchain name to be processed by
 * @see io.openfuture.state.watcher.BlockchainProcessor
 */
@Component
class BlockchainChecker(
    private val blockchains: List<Blockchain>,
    private val processingRepository: ProcessingRedisRepository
) {

    @Scheduled(fixedDelayString = "#{@checkDelay}")
    fun check() = GlobalScope.launch {
        for (blockchain in blockchains) {
            val lastFetched = blockchain.getLastBlockNumber()
            val lastSaved = processingRepository.getLast(blockchain)
            if (lastFetched == lastSaved) {
                continue
            }

            log.info("A new block acquired: {}, #{}", blockchain, lastFetched)
            processingRepository.setLast(blockchain, lastFetched)
            processingRepository.queue(blockchain)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(BlockchainChecker::class.java)
    }
}
