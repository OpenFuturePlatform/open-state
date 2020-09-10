package io.openfuture.state.config

import io.openfuture.state.property.WatcherProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WatcherConfig(private val watcherProperties: WatcherProperties) {

    @Bean
    fun checkDelay(): Long = watcherProperties.checkDelay!!

    @Bean
    fun processDelay(): Long = watcherProperties.processDelay!!
}
