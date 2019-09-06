package io.openfuture.state.config.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotNull

@ConfigurationProperties(prefix = "ethereum")
@Validated
@Component
class EthereumProperties(
        @field:NotNull var eventSubscription: Boolean = true
)
