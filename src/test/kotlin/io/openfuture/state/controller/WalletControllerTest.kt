package io.openfuture.state.controller

import com.nhaarman.mockitokotlin2.given
import io.openfuture.state.base.ControllerTests
import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.config.BlockchainConfig
import io.openfuture.state.service.WalletService
import io.openfuture.state.util.createDummySaveWalletRequest
import io.openfuture.state.util.createDummyWallet
import kotlinx.coroutines.runBlocking
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import java.time.LocalDateTime

@WebFluxTest(WalletController::class, ExceptionHandler::class)
@Import(BlockchainConfig::class)
class WalletControllerTest : ControllerTests() {

    @MockBean
    private lateinit var walletService: WalletService

    @Autowired
    private lateinit var blockchains: List<Blockchain>

    @Test
    fun findByAddressShouldReturnWallet() = runBlocking<Unit> {
        val wallet = createDummyWallet(
                id = ObjectId("5f480720e5cba939f1918911"),
                lastUpdate = LocalDateTime.parse("2020-08-28T01:18:56.825261")
        )
        given(walletService.findByAddress("this-is-address")).willReturn(wallet)

        webClient.get()
                .uri("/api/wallets/address/this-is-address")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .json("""
                    {
                        "id": "5f480720e5cba939f1918911",
                        "address": "address",
                        "webhook": "webhook",
                        "lastUpdateDate": "2020-08-28T01:18:56.825261"
                    }
                """.trimIndent())
    }

    @Test
    fun saveShouldSaveAndReturnWallet() = runBlocking<Unit> {
        val wallet = createDummyWallet(
                id = ObjectId("5f480720e5cba939f1918911"),
                lastUpdate = LocalDateTime.parse("2020-08-28T01:18:56.825261")
        )

        val request = createDummySaveWalletRequest()

        given(walletService.save(blockchains.first(), request.address!!, request.webhook!!)).willReturn(wallet)

        webClient.post()
                .uri("/api/wallets")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .json("""
                    {
                        "id": "5f480720e5cba939f1918911",
                        "address": "address",
                        "webhook": "webhook",
                        "lastUpdateDate": "2020-08-28T01:18:56.825261"
                    }
                """.trimIndent())
    }
}
