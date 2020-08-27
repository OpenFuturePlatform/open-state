package io.openfuture.state.controller

import com.nhaarman.mockitokotlin2.given
import io.openfuture.state.base.ControllerTests
import io.openfuture.state.service.WalletService
import io.openfuture.state.util.createDummySaveWalletRequest
import io.openfuture.state.util.createDummyWalletDto
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean

@WebFluxTest(WalletController::class, ExceptionHandler::class)
class WalletControllerTest : ControllerTests() {

    @MockBean
    private lateinit var walletService: WalletService

    @Test
    fun findByAddressShouldReturnWallet() = runBlocking<Unit> {
        val walletDto = createDummyWalletDto()
        given(walletService.findByAddress("this-is-address")).willReturn(walletDto)

        webClient.get()
                .uri("/api/wallets/address/this-is-address")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .json(om.writeValueAsString(walletDto))
    }

    @Test
    fun saveShouldSaveAndReturnWallet() = runBlocking<Unit> {
        val walletDto = createDummyWalletDto()
        val request = createDummySaveWalletRequest()
        given(walletService.save(request)).willReturn(walletDto)

        webClient.post()
                .uri("/api/wallets")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .json(om.writeValueAsString(walletDto))
    }

}
