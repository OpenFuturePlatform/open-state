package io.openfuture.state.controller

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.binance.BinanceBlockchain
import io.openfuture.state.blockchain.bitcoin.BitcoinBlockchain
import io.openfuture.state.blockchain.ethereum.EthereumBlockchain
import io.openfuture.state.client.BinanceHttpClientApi
import io.openfuture.state.client.ExchangeRate
import io.openfuture.state.domain.CoinGateRate
import io.openfuture.state.domain.CurrencyCode
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

@RestController
@RequestMapping("/api/currency/rate")
class ExchangeRateController(
    val binanceHttpClientApi: BinanceHttpClientApi,
    val blockchains: List<Blockchain>
) {

    @GetMapping("/{baseTicker}")
    suspend fun getRate(@PathVariable baseTicker: String ): ExchangeRate {
//        for (blockchain in blockchains) {
//            if (blockchain.getName().lowercase().startsWith("EthereumBlockchain") || blockchain.getName().lowercase().startsWith("GoerliBlockchain")) {
//                val price = binanceHttpClientApi.getExchangeRate(blockchain).price
//                return BigDecimal.ONE.divide(price, price.scale(), RoundingMode.HALF_UP).stripTrailingZeros()
//            }
//        }
//        return BigDecimal.ONE
        val currencyCode = CurrencyCode.entries.find { it.code == baseTicker }
        return binanceHttpClientApi.getExchangeRate(currencyCode!!)
    }

    @GetMapping("/all")
    suspend fun getAllRates(@RequestParam("ticker", required = false) ticker: String?): Any {
        return binanceHttpClientApi.getAllRateFromApi(ticker)
    }

}