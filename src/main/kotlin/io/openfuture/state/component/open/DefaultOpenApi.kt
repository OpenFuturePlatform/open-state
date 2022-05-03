package io.openfuture.state.component.open

import io.openfuture.state.domain.CustomToken
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

    override suspend fun getTokens(): Array<CustomToken> {
        val url = "/token/list"
        val response = openRestTemplate.getForEntity(url, Array<CustomToken>::class.java)
        return response.body!!
    }
}