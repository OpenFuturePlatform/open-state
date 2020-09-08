package io.openfuture.state.watcher

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.repository.ProcessingRedisRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BlockchainChecker(
        private val blockchains: List<Blockchain>,
        private val processingRedisRepository: ProcessingRedisRepository
) {
    @Scheduled(fixedDelayString = "\${watcher.check-delay}")
    fun updateLastBlockNumbers() = GlobalScope.launch {
        for (blockchain in blockchains) {
            val lastFetched = blockchain.getLastBlockNumber()
            val lastSaved = processingRedisRepository.getLast(blockchain)
            if (lastFetched == lastSaved) {
                continue
            }

            processingRedisRepository.setLast(blockchain, lastFetched)
            processingRedisRepository.queue(blockchain)
        }
    }
}
