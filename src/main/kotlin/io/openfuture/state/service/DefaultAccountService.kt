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
        private val stateService: StateService
) : AccountService {

    @Transactional
    override fun save(account: Account, integrations: Set<CreateIntegrationRequest>): Account {
        val wallets = saveWallets(account, integrations)
        account.wallets.addAll(wallets)

        return repository.save(account)
    }

    @Transactional(readOnly = true)
    override fun get(id: Long): Account {
        return repository.findById(id).orElseGet { throw NotFoundException("Account with id $id not found") }
    }

    @Transactional
    override fun update(id: Long, webHook: String): Account {
        val account = get(id)

        account.webHook = webHook
        return repository.save(account)
    }

    @Transactional
    override fun addWallets(id: Long, integrations: Set<CreateIntegrationRequest>): Account {
        val account = get(id)
        return save(account, integrations)
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
    override fun deleteWallet(accountId: Long, walletId: Long): Account {
        val account = get(accountId)
        val wallet = walletService.get(walletId, account)

        walletService.deleteByAccount(account, wallet)
        account.wallets.remove(wallet)

        return repository.save(account)
    }

    private fun saveWallets(account: Account, integrations: Set<CreateIntegrationRequest>): List<Wallet> {
        return integrations.map {
            val blockchain = blockchainService.get(it.blockchainId)
            val wallet = createOrUpdateWallet(account, blockchain, it.address)

            // get current balance

            walletService.save(wallet)
        }
    }

    private fun createOrUpdateWallet(account: Account, blockchain: Blockchain, address: String): Wallet {
        val persistWallet = walletService.getByBlockchainAddress(blockchain.id, address)

        if (null == persistWallet) {
            val startHash = State.generateHash(address)
            val startState = stateService.save(State(root = startHash))

            return Wallet(mutableSetOf(account), blockchain, address, startState)
        }

        if (!persistWallet.isActive) {
            val state = updateState(persistWallet.state.id, persistWallet.address)

            persistWallet.startTrackingDate = state.date
            persistWallet.isActive = true
        }

        return persistWallet
    }

    private fun updateState(stateId: Long, walletAddress: String): State {
        val state = stateService.get(stateId)
        val startHash = State.generateHash(walletAddress)
        state.root = startHash

        state.date = Date().time
        return stateService.save(state)
    }

}
