package io.openfuture.state.controller

import io.openfuture.state.client.CoinGateHttpClientApi
import io.openfuture.state.client.ExchangeRate
import io.openfuture.state.domain.CurrencyCode
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/currency/rate")
class ExchangeRateController(
    val coinGateHttpClientApi: CoinGateHttpClientApi
) {

    @GetMapping("/{baseTicker}")
    suspend fun getRate(@PathVariable baseTicker: String ): ExchangeRate {
        val currencyCode = CurrencyCode.entries.find { it.code == baseTicker }
        return coinGateHttpClientApi.getExchangeRate(currencyCode!!)
    }

    @GetMapping("/all")
    suspend fun getAllRates(@RequestParam("ticker", required = false) ticker: String?): Any {
        return coinGateHttpClientApi.getAllRateFromApi(ticker)
    }

}