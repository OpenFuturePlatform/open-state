package io.openfuture.state.config

import io.openfuture.state.property.TronProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

@Configuration
class TronConfig {
    @Bean
    @ConditionalOnProperty(value = ["production.mode.enabled"], havingValue = "true")
    fun web3jTron(tronProperties: TronProperties): Web3j {
        return Web3j.build(HttpService(tronProperties.mainnetAddress))
    }

    @Bean
    @ConditionalOnMissingBean(name = ["tronClient"])
    fun web3jTronTestnet(tronProperties: TronProperties): Web3j {
        return Web3j.build(HttpService(tronProperties.testnetAddress))
    }

}
