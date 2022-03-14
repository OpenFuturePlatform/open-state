package io.openfuture.state.service.dto

import java.math.BigDecimal

data class PlaceOrderResponse(
    val webhook: String,
    val orderId: String,
    val orderKey: String,
    val amount: BigDecimal,
    val wallets: List<WalletCreateResponse>
)

data class WalletCreateResponse(
    val blockchain: String,
    val address: String,
    val rate: BigDecimal
)
