package io.openfuture.state.client

import io.openfuture.state.property.OpenApiProperties
import io.openfuture.state.service.DefaultWalletService
import io.openfuture.state.service.WebhookInvoker
import org.slf4j.LoggerFactory
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
        var signature: String?
        try {
            signature = restTemplate.postForObject("/api/application/wallet/sign/address/${address}", request, String::class.java)
            log.info("Got signature $signature")
        } catch (e: Exception) {
            log.error(e.message)
            signature = null
        }
        return signature
    }

    companion object {
        val log = LoggerFactory.getLogger(DefaultWalletService::class.java)
    }
}
