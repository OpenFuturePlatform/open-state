package io.openfuture.state.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.validation.annotation.Validated
import java.time.Duration

@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "webhook.invocation")
data class WebhookProperties(

        /**
         * Retry options for webhook invocation
         */
        @NestedConfigurationProperty
        val retryOptions: RetryProperties = RetryProperties(),

        /**
         * Delay for checking new invocations to process.
         */
        val processDelay: Duration = Duration.ofSeconds(15),

        /**
         * Lok TTL for wallet's webhook
         */
        val lockTtl: Duration = Duration.ofSeconds(60)
)
