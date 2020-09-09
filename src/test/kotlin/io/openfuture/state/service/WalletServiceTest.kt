package io.openfuture.state.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import io.openfuture.state.base.ServiceTests
import io.openfuture.state.domain.Wallet
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.WalletRepository
import io.openfuture.state.util.createDummyWallet
import io.openfuture.state.util.createDummyBlockchain
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import reactor.core.publisher.Mono

class WalletServiceTest : ServiceTests() {

    @Mock
    private lateinit var walletRepository: WalletRepository

    private lateinit var walletService: WalletService

    @BeforeEach
    fun setUp() {
        walletService = DefaultWalletService(walletRepository)
    }

    @Test
    fun saveShouldReturnWallet() = runBlocking<Unit> {
        val wallet = createDummyWallet()

        given(walletRepository.save(any<Wallet>())).willReturn(Mono.just(wallet))

        val result = walletService.save(createDummyBlockchain(), wallet.address, wallet.webhook)

        assertThat(result).isEqualTo(wallet)
    }

    @Test
    fun findByAddressShouldThrowNotFoundException() {
        given(walletRepository.findByAddress("address")).willReturn(Mono.empty())
        Assertions.assertThrows(NotFoundException::class.java) {
            runBlocking {
                walletService.findByAddress("address")
            }
        }
    }

    @Test
    fun findByAddressShouldReturnWalletDto() = runBlocking<Unit> {
        val wallet = createDummyWallet()

        given(walletRepository.findByAddress("address")).willReturn(Mono.just(wallet))

        val result = walletService.findByAddress("address")

        assertThat(result).isEqualTo(wallet)
    }
}
