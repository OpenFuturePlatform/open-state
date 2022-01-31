package io.openfuture.state.config

import io.openfuture.state.property.EthereumTestProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

@Configuration
class EthereumTestConfig {
    @Bean
    fun web3jTest(ethereumTestProperties: EthereumTestProperties): Web3j {
        return Web3j.build(HttpService(ethereumTestProperties.nodeAddress))
    }
}