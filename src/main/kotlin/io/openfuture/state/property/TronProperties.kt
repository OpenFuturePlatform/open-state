package io.openfuture.state.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated

@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "tron")
data class TronProperties (
    val mainnetAddress: String,
    val testnetAddress: String,
    val apiKey: String
)