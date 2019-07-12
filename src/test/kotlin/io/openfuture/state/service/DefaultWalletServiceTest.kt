package io.openfuture.state.service

import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.WalletRepository
import io.openfuture.state.util.createDummyAccount
import io.openfuture.state.util.createDummyBlockchain
import io.openfuture.state.util.createDummyWallet
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock

class DefaultWalletServiceTest {

    private val repository = mock(WalletRepository::class.java)

    private lateinit var walletService: WalletService


    @Before
    fun setUp() {
        walletService = DefaultWalletService(repository)
    }

    @Test
    fun saveShouldAddNewWallet() {
        val wallet = createDummyWallet()

        given(repository.save(wallet)).willReturn(wallet)

        val result = walletService.save(wallet)

        assertThat(result).isEqualTo(wallet)
    }

    @Test
    fun getShouldReturnWalletWhenExists() {
        val account = createDummyAccount()
        val wallet = createDummyWallet(mutableSetOf(account)).apply { id = 1 }

        given(repository.findByIdAndAccountsContains(wallet.id, account)).willReturn(wallet)

        val result = walletService.get(wallet.id, account)

        assertThat(result).isEqualTo(wallet)
    }

    @Test(expected = NotFoundException::class)
    fun getShouldThrowExceptionWhenWalletDoesNotExists() {
        val account = createDummyAccount()
        val wallet = createDummyWallet(mutableSetOf(account)).apply { id = 1 }

        given(repository.findByIdAndAccountsContains(wallet.id, account)).willReturn(null)

        walletService.get(wallet.id, account)
    }

    @Test
    fun getAllByAccount() {
        val account = createDummyAccount()
        val wallets = listOf(createDummyWallet(mutableSetOf(account)).apply { id = 1 })

        given(repository.findAllByAccountsContains(account)).willReturn(wallets)

        val result = walletService.getAllByAccount(account)

        assertThat(result).isEqualTo(wallets)
    }

    @Test
    fun getByBlockchainAddress() {
        val walletAddress = "wallet address"
        val blockchain = createDummyBlockchain().apply { id = 1 }
        val wallet = createDummyWallet(address = walletAddress)

        given(repository.findByBlockchainIdAndAddress(blockchain.id, wallet.address)).willReturn(wallet)

        val result = walletService.getByBlockchainAddress(blockchain.id, wallet.address)

        assertThat(result).isNotNull
        assertThat(result).isEqualTo(wallet)
        assertThat(result!!.address).isEqualTo(walletAddress)
    }

}
