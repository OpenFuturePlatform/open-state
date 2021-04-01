package io.openfuture.state.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import io.openfuture.state.base.ServiceTests
import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.domain.WalletIdentity
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.TransactionRepository
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
    private val transactionRepository: TransactionRepository = mock()
    private val webhookService: WebhookService = mock()
    private val blockchain: Blockchain = mock()


    @BeforeEach
    fun setUp() {
        walletService = DefaultWalletService(walletRepository, transactionRepository, webhookService)
        given(blockchain.getName()).willReturn("MockBlockchain")
    }

    @Test
    fun saveShouldReturnWallet() = runBlocking<Unit> {
        val wallet = createDummyWallet()

        given(walletRepository.save(any())).willReturn(Mono.just(wallet))

        val result = walletService.save(blockchain, wallet.identity.address, wallet.webhook)

        assertThat(result).isEqualTo(wallet)
    }

    @Test
    fun findByIdentityShouldThrowNotFoundException() {
        given(walletRepository.findByIdentity(WalletIdentity("Ethereum", "address"))).willReturn(Mono.empty())
        Assertions.assertThrows(NotFoundException::class.java) {
            runBlocking {
                walletService.findByIdentity("Ethereum", "address")
            }
        }
    }

    @Test
    fun findByIdentityShouldReturnWalletDto() = runBlocking<Unit> {
        val wallet = createDummyWallet()

        given(walletRepository.findByIdentity(WalletIdentity("Ethereum", "address"))).willReturn(Mono.just(wallet))

        val result = walletService.findByIdentity("Ethereum", "address")

        assertThat(result).isEqualTo(wallet)
    }
}
