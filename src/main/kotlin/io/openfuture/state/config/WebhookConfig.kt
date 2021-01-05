package io.openfuture.state.config

import io.openfuture.state.property.WebhookProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebhookConfig(private val properties: WebhookProperties) {

    fun maxAttempts(): Int = properties.maxAttempts

    @Bean
    fun webhookProcessDelay(): Long = properties.processDelay.toMillis()
}

