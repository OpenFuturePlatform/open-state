package io.openfuture.state.client

import java.math.BigDecimal

data class ExchangeRate(
    val symbol: String,
    val price: BigDecimal
)
