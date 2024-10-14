package io.openfuture.state.client

import io.openfuture.state.domain.CoinGateRate
import io.openfuture.state.domain.CurrencyCode
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class CoinGateHttpClientApi(builder: WebClient.Builder) {

    val client: WebClient = builder.build()

    suspend fun getExchangeRate(currencyCode: CurrencyCode): ExchangeRate {
        return when (currencyCode) {
            CurrencyCode.ETHEREUM ->
                getRateFromApi(currencyCode.code,"https://api.coingate.com/v2/rates/merchant/ETH/USDT")

            CurrencyCode.BITCOIN ->
                getRateFromApi(currencyCode.code, "https://api.coingate.com/v2/rates/merchant/BTC/USDT")

            CurrencyCode.BINANCE ->
                getRateFromApi(currencyCode.code,"https://api.coingate.com/v2/rates/merchant/BNB/USDT")

            CurrencyCode.TRON ->
                getRateFromApi(currencyCode.code,"https://api.coingate.com/v2/rates/merchant/TRX/USDT")

        }
    }

    suspend fun getRateFromApi(symbol: String, url: String): ExchangeRate {

        val rate = client
            .get()
            .uri(url)
            .retrieve()
            .toEntity(String::class.java)
            .awaitSingle()
            .body!!

        return ExchangeRate(symbol, rate.toBigDecimal())
    }

    suspend fun getAllRateFromApi(ticker: String?): Any {

        val rates = client
            .get()
            .uri("https://api.coingate.com/v2/rates/merchant")
            .retrieve()
            .toEntity(CoinGateRate::class.java)
            .awaitSingle()
            .body!!

        if (ticker != null){
            return when(ticker){
                "BNB" -> rates.BNB
                "BTC" -> rates.BTC
                "ETH" -> rates.ETH
                "TRX" -> rates.TRX
                else -> {}
            }
        }

        return rates
    }

}