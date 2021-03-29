package io.openfuture.state.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

@ConstructorBinding
@ConfigurationProperties(prefix = "binance")
@Validated
data class BinanceProperties(
        @field:NotBlank val nodeAddress: String = "https://dataseed1.ninicoin.io",
        @field:NotBlank val hrp: String = "bnb",
        @field:NotBlank val valHrp: String = "bva",
)
