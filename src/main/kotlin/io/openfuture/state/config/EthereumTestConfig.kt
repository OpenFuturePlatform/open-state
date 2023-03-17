package io.openfuture.state.config

import io.openfuture.state.config.property.AlchemyProperties
import io.openfuture.state.config.property.AlchemyTestnetProperties
import io.openfuture.state.property.EthereumTestProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

@Configuration
class EthereumTestConfig {

    @Bean
    fun web3jTest(ethereumTestProperties: EthereumTestProperties,
                  alchemyTestnetProperties: AlchemyTestnetProperties): Web3j {
        return Web3j.build(HttpService(alchemyTestnetProperties.address))
//        return Web3j.build(HttpService(ethereumTestProperties.nodeAddress))
    }

}