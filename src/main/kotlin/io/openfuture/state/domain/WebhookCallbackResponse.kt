package io.openfuture.state.domain

import java.math.BigDecimal

data class WebhookCallbackResponse(
    val orderId: String,
    val lastPaid: BigDecimal,
    val totalPaid: BigDecimal,
    val remaining: BigDecimal,
    val status: String,
    val address: String,
    val currency: String
)