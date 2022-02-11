package io.openfuture.state.controller

import com.nhaarman.mockitokotlin2.given
import io.openfuture.state.base.ControllerTests
import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.service.WalletService
import io.openfuture.state.util.createDummyWallet
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import java.math.BigDecimal
import java.time.LocalDateTime

@WebFluxTest(WalletController::class, ExceptionHandler::class)
class WalletControllerTest : ControllerTests() {

    @MockBean
    private lateinit var walletService: WalletService

    @MockBean
    private lateinit var blockchain: Blockchain

    @Test
    fun findByAddressShouldReturnWallet() = runBlocking<Unit> {
        val wallet = createDummyWallet(
            id = "5f480720e5cba939f1918911",
            lastUpdate = LocalDateTime.parse("2020-08-28T01:18:56.825261")
        )
        given(walletService.findByIdentity("this-is-chain", "this-is-address")).willReturn(wallet)

        webClient.get()
            .uri("/api/wallets/blockchain/this-is-chain/address/this-is-address")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .json(
                """
                    {
                        "id": "5f480720e5cba939f1918911",
                        "address": "address",
                        "webhook": "webhook",
                        "lastUpdateDate": "2020-08-28T01:18:56.825261"
                    }
                """.trimIndent()
            )
    }

    @Test
    @Disabled
    fun saveShouldSaveAndReturnWallet() = runBlocking<Unit> {
        val wallet = createDummyWallet(
            id = "5f480720e5cba939f1918911",
            lastUpdate = LocalDateTime.parse("2020-08-28T01:18:56.825261")
        )

        given(blockchain.getName()).willReturn("Ethereum")
        given(
            walletService.save(
                blockchain, WalletController.SaveWalletRequest(
                    "address", "webhook", "ethereum",
                    WalletController.WalletMetaDataRequest("OrderId", "OrderKey", BigDecimal.TEN, "USD", "Woocommerce", "ETH")
                )
            )
        ).willReturn(wallet)

        webClient.post()
            .uri("/api/wallets")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                    {
                        "webhook": "http://locahost:8086",
                        "address": "0xC2116EA27AB41BaA4f6F8F67cBdCe3d241251a46",
                        "blockchain": "Ethereum",
                        "metadata": {
                            "orderId": "#3",
                            "orderKey": "#2004",
                            "amount": 500,
                            "productCurrency": "USD",
                            "source": "Woocommerce #5",
                            "paymentCurrency": "ETH"
                        }
                    }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .json(
                """
                    {
                        "id": "5f480720e5cba939f1918911",
                        "address": "address",
                        "webhook": "webhook",
                        "lastUpdateDate": "2020-08-28T01:18:56.825261"
                    }
                """.trimIndent()
            )
    }

    @Test
    fun updateShouldUpdateAndReturnWallet() = runBlocking<Unit> {
        val wallet = createDummyWallet(
            id = "5f480720e5cba939f1918911",
            webhook = "https://www.openfuture.io/",
            lastUpdate = LocalDateTime.parse("2020-08-28T01:18:56.825261")
        )

        given(walletService.update(wallet.id.toString(), wallet.webhook)).willReturn(wallet)

        webClient.put()
            .uri("/api/wallets/${wallet.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                    {
                        "webhook": "https://www.openfuture.io/"
                    }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .json(
                """
                    {
                        "id": "5f480720e5cba939f1918911",
                        "address": "address",
                        "webhook": "https://www.openfuture.io/",
                        "lastUpdateDate": "2020-08-28T01:18:56.825261"
                    }
                """.trimIndent()
            )
    }

    @Test
    fun updateGivenInvalidWebhookShouldReturnBadRequest() = runBlocking<Unit> {
        webClient.put()
            .uri("/api/wallets/id")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                    {
                        "webhook": "not valid url"
                    }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isBadRequest
    }

}
