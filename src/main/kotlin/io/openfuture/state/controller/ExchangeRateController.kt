package io.openfuture.state.controller

import io.openfuture.state.client.BinanceHttpClientApi
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.math.RoundingMode

@RestController
@RequestMapping("/api/currency/rate")
class ExchangeRateController(val binanceHttpClientApi: BinanceHttpClientApi) {

    @GetMapping("/ethereum")
    suspend fun getRate(): BigDecimal {
        val price = binanceHttpClientApi.getEthereumRate().price
        return BigDecimal.ONE.divide(price, price.scale(), RoundingMode.HALF_UP)
    }

}