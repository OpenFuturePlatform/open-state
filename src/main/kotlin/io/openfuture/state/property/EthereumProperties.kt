package io.openfuture.state.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@ConstructorBinding
@ConfigurationProperties(prefix = "ethereum")
@Validated
data class EthereumProperties(@field:NotNull @field:NotEmpty val nodeAddress: String?)
