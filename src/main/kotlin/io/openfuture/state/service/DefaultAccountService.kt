package io.openfuture.state.service

import io.openfuture.state.domain.request.CreateIntegrationRequest
import io.openfuture.state.entity.Account
import io.openfuture.state.entity.State
import io.openfuture.state.entity.Wallet
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.AccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultAccountService(
        private val repository: AccountRepository,
        private val blockchainService: BlockchainService,
        private val walletService: WalletService,
        private val stateService: StateService
) : AccountService {

    @Transactional
    override fun create(webHook: String, integrations: Set<CreateIntegrationRequest>) {
        val account = repository.save(Account(webHook))

        createWallets(account, integrations)
    }

    @Transactional(readOnly = true)
    override fun get(id: Long): Account {
        return repository.findById(id).orElseGet { throw NotFoundException("Account with id $id not found") }
    }

    @Transactional
    override fun update(id: Long, webHook: String): Account {
        val account = get(id)

        account.webhook = webHook

        return repository.save(account)
    }

    @Transactional
    override fun addWallets(id: Long, integrations: Set<CreateIntegrationRequest>) {
        val account = get(id)
        createWallets(account, integrations)

        repository.save(account)
    }

    private fun createWallets(account: Account, integrations: Set<CreateIntegrationRequest>) {
        integrations.forEach {
            val blockchain = blockchainService.get(it.blockchainId)
            val startState = stateService.save(State(root = "start root"))
            walletService.save(Wallet(account, blockchain, it.address, startState))
        }

    }

}
