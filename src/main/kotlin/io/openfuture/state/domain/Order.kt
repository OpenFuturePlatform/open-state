package io.openfuture.state.domain

import java.math.BigDecimal

data class Order(
    val orderId: String,
    val orderKey: String,
    val amount: BigDecimal,
    val productCurrency: String,
    val source: String,
    val webhook: String
)
