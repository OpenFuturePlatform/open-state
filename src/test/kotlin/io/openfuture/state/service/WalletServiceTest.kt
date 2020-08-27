package io.openfuture.state.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import io.openfuture.state.base.ServiceTests
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.mapper.WalletMapper
import io.openfuture.state.model.Wallet
import io.openfuture.state.repository.WalletRepository
import io.openfuture.state.util.createDummySaveWalletRequest
import io.openfuture.state.util.createDummyWallet
import io.openfuture.state.util.createDummyWalletDto
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

    @Mock
    private lateinit var walletMapper: WalletMapper

    private lateinit var walletService: WalletService

    @BeforeEach
    fun setUp() {
        walletService = WalletServiceImpl(
                walletRepository = walletRepository,
                walletMapper = walletMapper
        )
    }

    @Test
    fun saveReturnWalletDto() = runBlocking<Unit> {
        val wallet = createDummyWallet()
        val request = createDummySaveWalletRequest()
        val walletDto = createDummyWalletDto()

        given(walletRepository.save(any<Wallet>())).willReturn(Mono.just(wallet))
        given(walletMapper.toWalletDto(wallet)).willReturn(walletDto)

        val result = walletService.save(request)

        assertThat(result).isEqualTo(walletDto)
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
        val walletDto = createDummyWalletDto()

        given(walletRepository.findByAddress("address")).willReturn(Mono.just(wallet))
        given(walletMapper.toWalletDto(wallet)).willReturn(walletDto)

        val result = walletService.findByAddress("address")

        assertThat(result).isEqualTo(walletDto)
    }
}
