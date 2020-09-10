package io.openfuture.state.config

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@EnableAsync
@Configuration
class AsyncConfig
