package io.openfuture.state.client

import io.openfuture.state.blockchain.Blockchain
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.math.BigDecimal

@Component
class BinanceHttpClientApi(builder: WebClient.Builder) {

    val client: WebClient = builder.build()

    suspend fun getExchangeRate(blockchain: Blockchain): ExchangeRate {
        return when (blockchain.getName()) {
            "EthereumBlockchain" ->
                getRateFromApi("https://api.binance.com/api/v3/ticker/price?symbol=ETHUSDT")
            "RopstenBlockchain" ->
                getRateFromApi("https://api.binance.com/api/v3/ticker/price?symbol=ETHUSDT")
            "BitcoinBlockchain" ->
                getRateFromApi("https://api.binance.com/api/v3/ticker/price?symbol=BTCUSDT")
            "BinanceBlockchain" ->
                getRateFromApi("https://api.binance.com/api/v3/ticker/price?symbol=BNBUSDT")
            else -> {
                ExchangeRate("UNKNOWN", BigDecimal.ONE)
            }
        }
    }

    suspend fun getRateFromApi(url: String): ExchangeRate {
        val response = client.get().uri(url)
            .exchange().awaitSingle()

        return response.toEntity(ExchangeRate::class.java).awaitSingle().body!!
    }

}