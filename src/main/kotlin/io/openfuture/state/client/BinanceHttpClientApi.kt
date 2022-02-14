package io.openfuture.state.client

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class BinanceHttpClientApi(builder: WebClient.Builder) {

    val client: WebClient = builder.build()

    suspend fun getEthereumRate(): ExchangeRate {
        val response = client.get().uri("https://api.binance.com/api/v3/ticker/price?symbol=ETHUSDT")
            .exchange().awaitSingle()

        return response.toEntity(ExchangeRate::class.java).awaitSingle().body!!
    }


}