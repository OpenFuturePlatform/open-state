package io.openfuture.state.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@ConstructorBinding
@ConfigurationProperties(prefix = "ropsten")
@Validated
data class EthereumTestProperties(@field:NotNull @field:NotBlank val nodeAddress: String?)
