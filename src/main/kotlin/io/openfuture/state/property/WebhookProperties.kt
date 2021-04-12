package io.openfuture.state.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.validation.annotation.Validated
import java.time.Duration

@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "webhook-invocation")
data class WebhookProperties(

    /**
     * Delay between attempts to execute scheduled webhooks
     * in wallets queue in seconds (default 5 sec)
     */
    val invocationProcessDelay: Duration = Duration.ofSeconds(5),

    /**
     * Lock timeout for wallets that process webhook
     * invocation in seconds. During that time another
     * instances can't process locked wallet
     * (default 300 sec)
     */
    val lockTTL: Duration = Duration.ofSeconds(300)
) {
    @Bean
    fun webhookInvocationProcessDelay(): Long {
        return invocationProcessDelay.toMillis()
    }
}
