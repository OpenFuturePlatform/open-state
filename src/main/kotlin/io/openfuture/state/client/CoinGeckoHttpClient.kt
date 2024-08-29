package io.openfuture.state.client

import org.springframework.stereotype.Component

@Component
class CoinGeckoHttpClient : CoinGeckoApiClient {
    override fun ping(): Ping? {
        TODO("Not yet implemented")
    }

    override fun getExchangeRates(): ExchangeRates? {
        TODO("Not yet implemented")
    }
}