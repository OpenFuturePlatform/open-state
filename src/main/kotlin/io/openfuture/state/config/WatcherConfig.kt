package io.openfuture.state.config

import io.openfuture.state.property.WatcherProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WatcherConfig(private val properties: WatcherProperties) {

    @Bean
    fun checkDelay(): Long = properties.checkDelay.toMillis()

    @Bean
    fun processDelay(): Long = properties.processDelay.toMillis()

}
