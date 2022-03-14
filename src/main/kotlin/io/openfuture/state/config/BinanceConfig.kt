package io.openfuture.state.config

import com.binance.dex.api.client.BinanceDexApiClientFactory
import com.binance.dex.api.client.BinanceDexApiNodeClient
import com.binance.dex.api.client.BinanceDexEnvironment
import io.openfuture.state.property.BinanceProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BinanceConfig {

    @Bean
    @ConditionalOnProperty(value = ["production.mode.enabled"], havingValue = "true")
    fun binanceClient(properties: BinanceProperties): BinanceDexApiNodeClient {
        return BinanceDexApiClientFactory.newInstance().newNodeRpcClient(
            BinanceDexEnvironment.PROD.nodeUrl,
            BinanceDexEnvironment.PROD.hrp,
            BinanceDexEnvironment.PROD.valHrp
        )
    }

    @Bean
    @ConditionalOnMissingBean(name = ["binanceClient"])
    fun binanceTestClient(properties: BinanceProperties): BinanceDexApiNodeClient {
        return BinanceDexApiClientFactory.newInstance().newNodeRpcClient(
            properties.testRpcEndpoints[0],
            BinanceDexEnvironment.TEST_NET.hrp,
            BinanceDexEnvironment.TEST_NET.valHrp
        )
    }

}
