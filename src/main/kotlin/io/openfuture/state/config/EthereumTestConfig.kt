package io.openfuture.state.config

import io.openfuture.state.property.EthereumAlchemyTestnetProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

@Configuration
class EthereumTestConfig {

    @Bean
    fun web3jTest(ethereumAlchemyTestnetProperties: EthereumAlchemyTestnetProperties): Web3j {
        return Web3j.build(HttpService(ethereumAlchemyTestnetProperties.address))
    }

}