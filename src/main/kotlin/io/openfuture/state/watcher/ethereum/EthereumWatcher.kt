package io.openfuture.state.watcher.ethereum

import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["watcher.ethereum.enabled"])
class EthereumWatcher(private val ethereumBlockProcessor: EthereumBlockProcessor) {

    @Scheduled(fixedDelayString = "\${watcher.ethereum.fixed-delay}")
    fun start() = runBlocking {
        ethereumBlockProcessor.processNext()
    }
}
