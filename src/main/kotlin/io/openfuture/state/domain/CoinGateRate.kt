package io.openfuture.state.domain

import com.fasterxml.jackson.annotation.JsonProperty


data class CoinGateRate(
    @JsonProperty("BTC")
    val BTC: CoinGateExchangeRate,
    @JsonProperty("BNB")
    val BNB: CoinGateExchangeRate,
    @JsonProperty("TRX")
    val TRX: CoinGateExchangeRate,
    @JsonProperty("ETH")
    val ETH: CoinGateExchangeRate,
    @JsonProperty("SOL")
    val SOL: CoinGateExchangeRate
)

data class CoinGateExchangeRate(
    @JsonProperty("USDT")
    val USDT: String
)