package io.openfuture.state.config

import io.openfuture.state.property.WebhookProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebhookConfig(private val properties: WebhookProperties) {

    fun progressiveAttempts(): Int = properties.retryOptions.progressiveMaxAttempts

    fun dailyAttempts(): Int = properties.retryOptions.dailyMaxAttempts

    fun maxAttempts(): Int = progressiveAttempts() + dailyAttempts()

    @Bean
    fun webhookProcessDelay(): Long = properties.processDelay.toMillis()
}

