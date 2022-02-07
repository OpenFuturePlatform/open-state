package io.openfuture.state.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.bitcoinj.core.Ping


interface CoinGeckoApiClient {

    fun ping(): Ping?

    fun getExchangeRates(): ExchangeRates?

}

@JsonIgnoreProperties(ignoreUnknown = true)
class Ping {

    @JsonProperty("gecko_says")
    private val geckoSays: String? = null

}

@JsonIgnoreProperties(ignoreUnknown = true)
class Rate {

    @JsonProperty("name")
    private val name: String? = null

    @JsonProperty("unit")
    private val unit: String? = null

    @JsonProperty("value")
    private val value = 0.0

    @JsonProperty("type")
    private val type: String? = null

}

@JsonIgnoreProperties(ignoreUnknown = true)
class ExchangeRates {

    @JsonProperty("rates")
    private val rates: Map<String, ExchangeRate>? = null

}
