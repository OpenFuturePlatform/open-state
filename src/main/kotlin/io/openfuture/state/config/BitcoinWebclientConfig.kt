package io.openfuture.state.config

import io.openfuture.state.property.BitcoinProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient


@Configuration
class BitcoinWebclientConfig {

    @Bean
    fun bitcoinWebClient(properties: BitcoinProperties): WebClient = WebClient.builder()
            .codecs { it.defaultCodecs().maxInMemorySize(16 * 1024 * 1024) }
            .baseUrl(properties.nodeAddress!!)
            .defaultHeaders { it.setBasicAuth(properties.username!!, properties.password!!) }
            .build()

}
