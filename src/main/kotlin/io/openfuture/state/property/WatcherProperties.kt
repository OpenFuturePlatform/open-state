package io.openfuture.state.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotNull

@ConstructorBinding
@ConfigurationProperties(prefix = "watcher")
@Validated
data class WatcherProperties(
        @field:NotNull val checkDelay: Long?,
        @field:NotNull val processDelay: Long?,
        @field:NotNull val lock: Lock?
) {
    data class Lock(val ttl: Long)
}
