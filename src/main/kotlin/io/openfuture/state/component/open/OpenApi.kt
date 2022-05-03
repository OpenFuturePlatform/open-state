package io.openfuture.state.component.open

import io.openfuture.state.domain.CustomToken
import io.openfuture.state.webhook.WebhookPayloadDto

interface OpenApi {

    suspend fun generateSignature(address: String, woocommerceDto: WebhookPayloadDto.WebhookWoocommerceDto): String
    suspend fun getTokens(): Array<CustomToken>
}