package io.openfuture.state.service

import io.openfuture.state.domain.request.CreateIntegrationRequest
import io.openfuture.state.entity.State
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.AccountRepository
import io.openfuture.state.util.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.internal.verification.VerificationModeFactory.times
import java.util.*

class DefaultAccountServiceTest {

    private val repository: AccountRepository = mock(AccountRepository::class.java)
    private val blockchainService: BlockchainService = mock(BlockchainService::class.java)
    private val walletService: WalletService = mock(WalletService::class.java)
    private val stateService: StateService = mock(StateService::class.java)

    private lateinit var accountService: AccountService


    @Before
    fun setUp() {
        accountService = DefaultAccountService(repository, blockchainService, walletService, stateService)
    }

    @Test
    fun saveShouldAddNewAccountWithNewWalletWhenWalletDoesNotTracked() {
        val blockchain = createDummyBlockchain().apply { id = 1 }
        val wallet = createDummyWallet(address = "unknown address")
        val account = createDummyAccount(wallets = mutableSetOf(wallet))

        given(blockchainService.get(blockchain.id)).willReturn(blockchain)
        given(walletService.getByBlockchainAddress(blockchain.id, wallet.address)).willReturn(null)
        given(stateService.save(any(State::class.java))).willReturn(createDummyState())
        given(walletService.save(wallet)).willReturn(wallet)
        given(repository.save(account)).willReturn(account)

        val integration = CreateIntegrationRequest(wallet.address, blockchain.id)
        val result = accountService.save(account, setOf(integration))

        assertThat(result).isEqualTo(account)
        assertThat(result.wallets).contains(wallet)
    }

    @Test
    fun saveShouldAddNewAccountWithWalletWhenWalletIsTrackedYet() {
        val blockchain = createDummyBlockchain().apply { id = 1 }
        val wallet = createDummyWallet()
        val account = createDummyAccount()

        given(blockchainService.get(blockchain.id)).willReturn(blockchain)
        given(walletService.getByBlockchainAddress(blockchain.id, wallet.address)).willReturn(wallet)
        verify(stateService, never()).save(any(State::class.java))
        given(walletService.save(wallet)).willReturn(wallet)
        given(repository.save(account)).willReturn(account)

        val integration = CreateIntegrationRequest(wallet.address, blockchain.id)
        val result = accountService.save(account, setOf(integration))

        assertThat(result).isEqualTo(account)
        assertThat(result.wallets).contains(wallet)
    }

    @Test
    fun getShouldReturnAccountWhenAccountExists() {
        val account = createDummyAccount().apply { id = 1 }

        given(repository.findById(account.id)).willReturn(Optional.of(account))

        val result = accountService.get(account.id)

        assertThat(result).isEqualTo(account)
    }

    @Test(expected = NotFoundException::class)
    fun getShouldThrowNotFoundExceptionWhenAccountNotExists() {
        val account = createDummyAccount().apply { id = 1 }

        given(repository.findById(account.id)).willReturn(Optional.empty())

        accountService.get(account.id)
    }

    @Test
    fun updateShouldUpdateWebHookWhenAccountExists() {
        val webHook = "http://updated.com"
        val account = createDummyAccount(webHook = webHook).apply { id = 1 }

        given(repository.findById(account.id)).willReturn(Optional.of(account))
        given(repository.save(account)).willReturn(account)

        val result = accountService.update(account.id, webHook)

        assertThat(result.webHook).isEqualTo(webHook)
    }

    @Test(expected = NotFoundException::class)
    fun updateShouldThrowNotFoundExceptionWhenAccountNotExists() {
        val webHook = "http://updated.com"
        val account = createDummyAccount(webHook = webHook).apply { id = 1 }

        given(repository.findById(account.id)).willReturn(Optional.empty())

        accountService.update(account.id, webHook)
    }

    @Test
    fun addWalletsShouldAddWalletToExistAccount() {
        val blockchain = createDummyBlockchain().apply { id = 1 }
        val wallet = createDummyWallet(address = "wallet address")
        val account = createDummyAccount(wallets = mutableSetOf(wallet)).apply { id = 1 }

        given(repository.findById(account.id)).willReturn(Optional.of(account))
        given(blockchainService.get(blockchain.id)).willReturn(blockchain)
        given(walletService.getByBlockchainAddress(blockchain.id, wallet.address)).willReturn(null)
        given(stateService.save(any(State::class.java))).willReturn(createDummyState())
        given(walletService.save(wallet)).willReturn(wallet)
        given(repository.save(account)).willReturn(account)

        val integration = CreateIntegrationRequest(wallet.address, blockchain.id)
        val result = accountService.addWallets(account.id, setOf(integration))

        assertThat(result).isEqualTo(account)
        assertThat(result.wallets).contains(wallet)
    }

    @Test
    fun addWalletsShouldAddWalletWithUpdatedStateIfWalletIsNotActive() {
        val blockchain = createDummyBlockchain().apply { id = 1 }
        val wallet = createDummyWallet(isActive = false)
        val account = createDummyAccount(wallets = mutableSetOf(wallet)).apply { id = 1 }

        given(repository.findById(account.id)).willReturn(Optional.of(account))
        given(blockchainService.get(blockchain.id)).willReturn(blockchain)
        given(walletService.getByBlockchainAddress(blockchain.id, wallet.address)).willReturn(wallet)
        given(stateService.get(wallet.state.id)).willReturn(createDummyState())
        given(stateService.save(any(State::class.java))).willReturn(createDummyState())
        given(walletService.save(wallet)).willReturn(wallet)
        given(repository.save(account)).willReturn(account)

        val integration = CreateIntegrationRequest(wallet.address, blockchain.id)
        val result = accountService.addWallets(account.id, setOf(integration))

        assertThat(result).isEqualTo(account)
        assertThat(result.wallets).contains(wallet)
    }

    @Test
    fun deleteAccountShouldDisableAccount() {
        val wallet = createDummyWallet()
        val account = createDummyAccount(wallets = mutableSetOf(wallet), isEnabled = false).apply { id = 1 }

        given(repository.findById(account.id)).willReturn(Optional.of(account))
        given(repository.save(account)).willReturn(account)

        val result = accountService.delete(account.id)

        verify(walletService, times(1)).deleteAllByAccount(account)

        assertThat(result.isEnabled).isFalse()
    }

    @Test
    fun deleteWalletShouldRemoveGivenWallet() {
        val wallet = createDummyWallet().apply { id = 1 }
        val account = createDummyAccount(wallets = mutableSetOf()).apply { id = 1 }

        given(repository.findById(account.id)).willReturn(Optional.of(account))
        given(walletService.get(wallet.id, account)).willReturn(wallet)
        given(repository.save(account)).willReturn(account)

        val result = accountService.deleteWallet(account.id, wallet.id)

        assertThat(result.wallets).isEmpty()
    }

}
