package io.openfuture.state.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated

@ConstructorBinding
@ConfigurationProperties(prefix = "binance")
@Validated
data class BinanceProperties(
        val mainnetNodeAddresses: List<String>,
        val testnetNodeAddresses: List<String>
)
