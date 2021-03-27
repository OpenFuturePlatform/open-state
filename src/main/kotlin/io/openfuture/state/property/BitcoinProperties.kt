package io.openfuture.state.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@ConstructorBinding
@ConfigurationProperties(prefix = "bitcoin")
@Validated
data class BitcoinProperties(
        @field:NotNull @field:NotBlank val nodeAddress: String?,
        @field:NotNull @field:NotBlank val username: String?,
        @field:NotNull @field:NotBlank val password: String?,
)
