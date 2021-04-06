package io.openfuture.state.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

@ConstructorBinding
@ConfigurationProperties(prefix = "bitcoin")
@Validated
data class BitcoinProperties(
    @field:NotBlank
    val nodeAddress: String?,

    @field:NotBlank
    val username: String?,

    @field:NotBlank
    val password: String?,
)
