package io.openfuture.state.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AppProperties{
    @Value("\${production.mode.enabled}")
    lateinit var isProdEnabled: String
}
