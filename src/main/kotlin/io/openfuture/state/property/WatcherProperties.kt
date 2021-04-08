package io.openfuture.state.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import java.time.Duration

@ConstructorBinding
@ConfigurationProperties(prefix = "watcher")
@Validated
data class WatcherProperties(
    /**
     * Delay for checking for new blocks.
     */
    val checkDelay: Duration = Duration.ofSeconds(15),

    /**
     * Delay for checking blockchains to process.
     */
    val processDelay: Duration = Duration.ofSeconds(15),

    /**
     * Lock TTL.
     */
    val lockTtl: Duration = Duration.ofSeconds(60)
)
