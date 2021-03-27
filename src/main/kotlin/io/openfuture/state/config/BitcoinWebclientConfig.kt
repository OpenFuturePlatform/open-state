package io.openfuture.state.config

import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import io.openfuture.state.property.BitcoinProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.client.reactive.ReactorResourceFactory
import org.springframework.web.reactive.function.client.WebClient


@Configuration
class BitcoinWebclientConfig {

    @Bean
    fun resourceFactory() = ReactorResourceFactory().apply {
        isUseGlobalResources = false
    }

    @Bean
    fun bitcoinWebClient(properties: BitcoinProperties): WebClient = WebClient.builder()
            .codecs { it.defaultCodecs().maxInMemorySize(16 * 1024 * 1024) }
            .clientConnector(ReactorClientHttpConnector(resourceFactory()) { client ->
                client.tcpConfiguration { tcpClient ->
                    tcpClient.doOnConnected { connection ->
                        connection.addHandlerLast(ReadTimeoutHandler(10))
                        connection.addHandlerLast(WriteTimeoutHandler(10))
                    }
                }

                client.keepAlive(false)
            })
            .baseUrl(properties.nodeAddress!!)
            .defaultHeaders { it.setBasicAuth(properties.username!!, properties.password!!) }
            .build()

}
