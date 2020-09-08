package io.openfuture.state.config

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.util.createDummyBlockchain
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BlockchainConfig {

    @Bean
    fun blockchains(): List<Blockchain> {
        return listOf(createDummyBlockchain())
    }
}
