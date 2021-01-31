package io.openfuture.state.config

import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.client.reactive.ReactorResourceFactory
import org.springframework.web.reactive.function.client.WebClient


@Configuration
class WebClientConfig {

    @Bean
    fun resourceFactory() = ReactorResourceFactory().apply {
        isUseGlobalResources = false
    }

    @Bean
    fun webClient(): WebClient = WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(resourceFactory()) { client ->
                client.tcpConfiguration { tcpClient ->
                    tcpClient.doOnConnected { connection ->
                        connection.addHandlerLast(ReadTimeoutHandler(10))
                        connection.addHandlerLast(WriteTimeoutHandler(10))
                    }
                }

                val sslContext = SslContextBuilder
                        .forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE)
                        .build()

                client.secure { it.sslContext(sslContext) }
                client.keepAlive(false)
            })
            .build()

}
