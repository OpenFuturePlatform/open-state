package io.openfuture.state.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

@Configuration
class EthereumConfig {
    @Bean
    fun web3j(@Value("\${watcher.ethereum.node-address}") nodeAddress: String): Web3j {
        return Web3j.build(HttpService(nodeAddress))
    }
}
