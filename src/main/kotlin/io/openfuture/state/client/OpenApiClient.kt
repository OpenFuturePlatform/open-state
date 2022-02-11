package io.openfuture.state.client

import io.openfuture.state.property.OpenApiProperties
import io.openfuture.state.service.WebhookInvoker
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.DefaultUriBuilderFactory

@Component
class OpenApiClient(
    openApiProperties: OpenApiProperties,
) {

    private final val restTemplate = RestTemplate()

    init {
        restTemplate.uriTemplateHandler = DefaultUriBuilderFactory(openApiProperties.baseUrl)
    }

    fun getSignature(address: String, request: WebhookInvoker.StateSignRequest): String? {
        return restTemplate.postForObject("/sign/address/${address}", request, String::class.java)
    }

}
