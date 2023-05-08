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
            "EthereumBlockchain", "GoerliBlockchain" ->
                getRateFromApi("https://api.coingate.com/v2/rates/merchant/ETH/USDT")

            "BitcoinBlockchain" ->
                getRateFromApi("https://api.coingate.com/v2/rates/merchant/BTC/USDT")

            "BinanceBlockchain", "BinanceTestnetBlockchain" ->
                getRateFromApi("https://api.coingate.com/v2/rates/merchant/BNB/USDT")

            else -> {
                ExchangeRate("UNKNOWN", BigDecimal.ONE)
            }
        }
    }

    suspend fun getRateFromApi(url: String): ExchangeRate {
        val response = client.get().uri(url)
            .exchange().awaitSingle()

        val rate: BigDecimal = response.toEntity(BigDecimal::class.java).awaitSingle().body!!

        return ExchangeRate("", rate)
    }

}