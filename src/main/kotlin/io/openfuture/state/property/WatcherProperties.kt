package io.openfuture.state.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.Valid
import javax.validation.constraints.NotNull

@ConstructorBinding
@ConfigurationProperties(prefix = "watcher")
@Validated
data class WatcherProperties(
        @field:NotNull val checkDelay: Long? = 15000,
        @field:NotNull val processDelay: Long? = 15000,
        @field:Valid @field:NotNull val lock: Lock? = Lock()
) {
    data class Lock(@field:NotNull val ttl: Long? = 60)
}
