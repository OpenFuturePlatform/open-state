package io.openfuture.state.component.open

import io.openfuture.state.webhook.WebhookPayloadDto
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class DefaultOpenApi(
    private val openRestTemplate: RestTemplate
): OpenApi {
    override suspend fun generateSignature(address: String, woocommerceDto: WebhookPayloadDto.WebhookWoocommerceDto): String {
        val url = "/application/wallet/sign/address/${address}"
        val response = openRestTemplate.postForEntity(url, woocommerceDto, String::class.java)
        return response.body!!
    }
}