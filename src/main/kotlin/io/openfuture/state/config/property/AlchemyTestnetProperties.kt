package io.openfuture.state.config.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "alchemy.testnet")
data class AlchemyTestnetProperties (
    @field:NotNull @field:NotBlank val address: String?,
    @field:NotNull @field:NotBlank val apiKey: String?
)