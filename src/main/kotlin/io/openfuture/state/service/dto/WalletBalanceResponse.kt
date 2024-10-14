package io.openfuture.state.service.dto

import java.math.BigDecimal
data class WalletBalanceResponse(
    val blockchain: String,
    val address: String,
    val balance: BigDecimal
)
