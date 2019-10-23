package io.openfuture.state.service

import io.openfuture.state.controller.domain.request.CreateIntegrationRequest
import io.openfuture.state.entity.Account
import io.openfuture.state.entity.Blockchain
import io.openfuture.state.entity.State
import io.openfuture.state.entity.Wallet
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.AccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class DefaultAccountService(
        private val repository: AccountRepository,
        private val blockchainService: BlockchainService,
        private val walletService: WalletService,
        private val stateService: StateService,
        private val integrationService: IntegrationService
) : AccountService {

    @Transactional
    override fun save(account: Account, integrations: Set<CreateIntegrationRequest>): Account {
        val wallets = saveWallets(account, integrations)
        account.wallets.addAll(wallets)

        return repository.save(account)
    }

    @Transactional(readOnly = true)
    override fun get(id: Long): Account {
        return repository.findByIdAndIsEnabledTrue(id) ?: throw NotFoundException("Account with id $id not found")
    }

    @Transactional
    override fun update(id: Long, webHook: String): Account {
        val account = get(id)

        account.webHook = webHook
        return repository.save(account)
    }

    @Transactional
    override fun delete(id: Long): Account {
        val account = get(id)
        account.isEnabled = false

        walletService.deleteAllByAccount(account)
        account.wallets.clear()

        return repository.save(account)
    }

    @Transactional
    override fun addWallets(id: Long, integrations: Set<CreateIntegrationRequest>): Account {
        val account = get(id)
        return save(account, integrations)
    }

    @Transactional
    override fun deleteWallet(accountId: Long, walletId: Long): Account {
        val account = get(accountId)
        val wallet = walletService.get(walletId, account)

        walletService.deleteByAccount(account, wallet)
        account.wallets.remove(wallet)

        return repository.save(account)
    }

    @Transactional
    override fun deleteWalletByAddress(accountId: Long, address: String, blockchainId: Long): Account {
        val account = get(accountId)
        val wallet = walletService.getByBlockchainAddress(blockchainId, address)

        wallet?.let { walletService.deleteByAccount(account, it) }
        account.wallets.remove(wallet)

        return repository.save(account)
    }

    private fun saveWallets(account: Account, integrations: Set<CreateIntegrationRequest>): List<Wallet> {
        return integrations.map {
            val blockchain = blockchainService.get(it.blockchainId)

            createOrUpdateWallet(account, blockchain, it.address)
        }
    }

    private fun createOrUpdateWallet(account: Account, blockchain: Blockchain, address: String): Wallet {
        val persistWallet = walletService.getByBlockchainAddress(blockchain.id, address)
                ?: return createWallet(address, account, blockchain)

        if (!persistWallet.isActive) {
            return updateWallet(persistWallet)
        }

        return persistWallet
    }

    private fun createWallet(address: String, account: Account, blockchain: Blockchain): Wallet {
        val balance = integrationService.getBalance(address, blockchain)

        val startState = stateService.save(State(balance, State.generateHash(address)))

        val wallet = Wallet(mutableSetOf(account), blockchain, address, startState)

        return walletService.save(wallet)
    }

    private fun updateWallet(wallet: Wallet): Wallet {
        val state = updateState(wallet.state.id, wallet.address, wallet.blockchain)

        wallet.startTrackingDate = state.date
        wallet.isActive = true

        return walletService.save(wallet)
    }

    private fun updateState(stateId: Long, walletAddress: String, blockchain: Blockchain): State {
        val state = stateService.get(stateId)

        state.root = State.generateHash(walletAddress)
        state.date = Date().time
        state.balance = integrationService.getBalance(walletAddress, blockchain)

        return stateService.save(state)
    }

}
