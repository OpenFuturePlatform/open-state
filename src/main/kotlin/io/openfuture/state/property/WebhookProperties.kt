package io.openfuture.state.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import java.time.Duration

@ConstructorBinding
@ConfigurationProperties(prefix = "webhook.invokation")
@Validated
data class WebhookProperties(

        /**
         * Max attempts count to invoke webhook request.
         */
        val maxAttempts: Int = 10,

        /**
         * Delay for checking new invocations to process.
         */
        val processDelay: Duration = Duration.ofSeconds(15),

        /**
         * Lok TTL for wallet's webhook
         */
        val lockTtl: Duration = Duration.ofSeconds(60)
)
