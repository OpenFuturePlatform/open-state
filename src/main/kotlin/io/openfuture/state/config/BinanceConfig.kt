package io.openfuture.state.config

import com.binance.dex.api.client.BinanceDexApiClientFactory
import com.binance.dex.api.client.BinanceDexApiNodeClient
import io.openfuture.state.property.BinanceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BinanceConfig {

    @Bean
    fun binanceClient(properties: BinanceProperties): BinanceDexApiNodeClient {
        return BinanceDexApiClientFactory.newInstance()
                .newNodeRpcClient(
                        properties.nodeAddress,
                        properties.hrp,
                        properties.valHrp
                )
    }

}
