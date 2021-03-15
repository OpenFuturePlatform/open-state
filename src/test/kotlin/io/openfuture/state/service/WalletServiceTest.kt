package io.openfuture.state.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import io.openfuture.state.base.ServiceTests
import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.domain.Wallet
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.WalletRepository
import io.openfuture.state.util.createDummyWallet
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono

class WalletServiceTest : ServiceTests() {


    private lateinit var walletService: WalletService

    private val walletRepository: WalletRepository = mock()
    private val transactionService: TransactionService = mock()
    private val blockchain: Blockchain = mock()
    private val webhookService: WebhookService = mock()


    @BeforeEach
    fun setUp() {
        walletService = DefaultWalletService(walletRepository, transactionService, webhookService)
        given(blockchain.getName()).willReturn("MockBlockchain")
    }

    @Test
    fun saveShouldReturnWallet() = runBlocking<Unit> {
        val wallet = createDummyWallet()

        given(walletRepository.save(any<Wallet>())).willReturn(Mono.just(wallet))

        val result = walletService.save(blockchain, wallet.address, wallet.webhook)

        assertThat(result).isEqualTo(wallet)
    }

    @Test
    fun findByBlockchainAndAddressShouldThrowNotFoundException() {
        given(walletRepository.findByBlockchainAndAddress("blockchain", "address")).willReturn(Mono.empty())
        Assertions.assertThrows(NotFoundException::class.java) {
            runBlocking {
                walletService.findByBlockchainAndAddress("blockchain", "address")
            }
        }
    }

    @Test
    fun findByBlockchainAndAddressShouldReturnWalletDto() = runBlocking<Unit> {
        val wallet = createDummyWallet()

        given(walletRepository.findByBlockchainAndAddress("EthereumBlockchain", "address")).willReturn(Mono.just(wallet))

        val result = walletService.findByBlockchainAndAddress("EthereumBlockchain", "address")

        assertThat(result).isEqualTo(wallet)
    }

    @Test
    fun updateShouldReturnWalletDto() = runBlocking<Unit> {
        val wallet = createDummyWallet()

        val id = wallet.id.toString()

        given(walletRepository.findById(id)).willReturn(Mono.just(wallet))
        given(walletRepository.save(wallet)).willReturn(Mono.just(wallet))

        val result = walletService.update(id, wallet.webhook)

        assertThat(result).isEqualTo(wallet)
    }

    @Test
    fun updateShouldThrowNotFoundException() = runBlocking<Unit> {
        val wallet = createDummyWallet()

        val id = wallet.id.toString()

        given(walletRepository.findById(id)).willReturn(Mono.empty())

        Assertions.assertThrows(NotFoundException::class.java) {
            runBlocking {
                walletService.update(id, wallet.webhook)
            }
        }
    }
}

