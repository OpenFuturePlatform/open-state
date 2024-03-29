package io.openfuture.state.config

import io.openfuture.state.property.EthereumAlchemyMainnetProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

@Configuration
class EthereumConfig {

    @Bean
    fun web3j(ethereumProperties: EthereumAlchemyMainnetProperties): Web3j {
        return Web3j.build(HttpService(ethereumProperties.address))
    }

}
