package io.openfuture.state.service

import io.openfuture.state.entity.Wallet
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.WalletRepository
import io.openfuture.state.util.any
import io.openfuture.state.util.createDummyAccount
import io.openfuture.state.util.createDummyBlockchain
import io.openfuture.state.util.createDummyWallet
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.*
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

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

        given(repository.findByIdAndAccountsContainsAndIsActiveTrue(wallet.id, account)).willReturn(wallet)

        val result = walletService.get(wallet.id, account)

        assertThat(result).isEqualTo(wallet)
    }

    @Test(expected = NotFoundException::class)
    fun getShouldThrowExceptionWhenWalletDoesNotExists() {
        val account = createDummyAccount()
        val wallet = createDummyWallet(mutableSetOf(account)).apply { id = 1 }

        given(repository.findByIdAndAccountsContainsAndIsActiveTrue(wallet.id, account)).willReturn(null)

        walletService.get(wallet.id, account)
    }

    @Test
    fun getAllByAccount() {
        val account = createDummyAccount()
        val wallets = listOf(createDummyWallet(mutableSetOf(account)).apply { id = 1 })

        given(repository.findAllByAccountsContainsAndIsActiveTrue(account)).willReturn(wallets)

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

    @Test
    fun getActiveByBlockchainAddress() {
        val blockchain = createDummyBlockchain().apply { id = 1 }
        val wallet = createDummyWallet()

        given(repository.findByBlockchainIdAndAddressAndIsActiveTrue(blockchain.id, wallet.address)).willReturn(wallet)

        val result = walletService.getActiveByBlockchainAddress(blockchain.id, wallet.address)

        assertThat(result).isNotNull
        assertThat(result).isEqualTo(wallet)
        assertThat(result!!.isActive).isTrue()
    }

    @Test
    fun deleteShouldDisableWalletIfDoesNotContainAccount() {
        val account = createDummyAccount().apply { id = 1 }
        val wallet = createDummyWallet(accounts = mutableSetOf(account))

        given(repository.findAllByAccountsContainsAndIsActiveTrue(account)).willReturn(listOf(wallet))

        walletService.deleteAllByAccount(account)

        verify(repository, Mockito.times(1)).save(any(Wallet::class.java))
    }

    @Test
    fun deleteAllShouldIgnoreDisablingWalletIfContainsAnotherAccounts() {
        val account1 = createDummyAccount().apply { id = 1 }
        val account2 = createDummyAccount().apply { id = 2 }
        val wallet = createDummyWallet(accounts = mutableSetOf(account1, account2))

        given(repository.findAllByAccountsContainsAndIsActiveTrue(account1)).willReturn(listOf(wallet))

        walletService.deleteAllByAccount(account1)

        verify(repository, never()).save(any(Wallet::class.java))
    }

    @Test
    fun deleteAllShouldIgnoreDisablingWalletsIfAccountDoesNotHaveWallets() {
        val account = createDummyAccount().apply { id = 1 }

        given(repository.findAllByAccountsContainsAndIsActiveTrue(account)).willReturn(emptyList())

        walletService.deleteAllByAccount(account)

        verify(repository, never()).save(any(Wallet::class.java))
    }

    @Test
    fun deleteByAccountShouldDisableWalletIfDoesNotContainAccount() {
        val account = createDummyAccount().apply { id = 1 }
        val wallet = createDummyWallet(accounts = mutableSetOf(account))

        given(repository.findAllByAccountsContainsAndIsActiveTrue(account)).willReturn(listOf(wallet))

        walletService.deleteByAccount(account, wallet)

        verify(repository, Mockito.times(1)).save(any(Wallet::class.java))
    }

}
