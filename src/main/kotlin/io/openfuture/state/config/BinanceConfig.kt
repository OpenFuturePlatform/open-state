package io.openfuture.state.config

import io.openfuture.state.property.BinanceProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

@Configuration
class BinanceConfig {

    @Bean
    @ConditionalOnProperty(value = ["production.mode.enabled"], havingValue = "true")
    fun web3jBinance(binanceProperties: BinanceProperties): Web3j {
        return Web3j.build(HttpService(binanceProperties.mainnetNodeAddresses[0]))
    }

    @Bean
    @ConditionalOnMissingBean(name = ["binanceClient"])
    fun web3jBinanceTestnet(binanceProperties: BinanceProperties): Web3j {
        return Web3j.build(HttpService(binanceProperties.testnetNodeAddresses[0]))
    }

}
