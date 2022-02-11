package io.openfuture.state.config

import io.openfuture.state.config.property.OpenApiProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.DefaultUriBuilderFactory

@Configuration
class OpenApiConfig {

    @Bean
    fun openRestTemplate(openApiProperties: OpenApiProperties): RestTemplate {
        val restTemplate = RestTemplate()
        restTemplate.uriTemplateHandler = DefaultUriBuilderFactory(openApiProperties.baseUrl!!)
        return restTemplate
    }
}