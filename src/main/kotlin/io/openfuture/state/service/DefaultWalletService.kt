package io.openfuture.state.service

import io.openfuture.state.entity.Account
import io.openfuture.state.entity.Wallet
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.WalletRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultWalletService(
        private val repository: WalletRepository
) : WalletService {

    @Transactional
    override fun save(wallet: Wallet): Wallet {
        return repository.save(wallet)
    }

    @Transactional(readOnly = true)
    override fun get(id: Long, account: Account): Wallet {
        return repository.findByIdAndAccountsContainsAndIsActiveTrue(id, account)
                ?: throw NotFoundException("Wallet with id $id not found")
    }

    @Transactional(readOnly = true)
    override fun getAllByAccount(account: Account): List<Wallet> {
        return repository.findAllByAccountsContainsAndIsActiveTrue(account)
    }

    @Transactional(readOnly = true)
    override fun getByBlockchainAddress(blockchainId: Long, address: String): Wallet? {
        return repository.findByBlockchainIdAndAddressIgnoreCase(blockchainId, address)
    }

    @Transactional(readOnly = true)
    override fun getActiveByBlockchainAddress(blockchainId: Long, address: String): Wallet? {
        return repository.findByBlockchainIdAndAddressIgnoreCaseAndIsActiveTrue(blockchainId, address)
    }

    @Transactional
    override fun deleteByAccount(account: Account, wallet: Wallet) {
        wallet.accounts.remove(account)

        if (wallet.accounts.isNotEmpty()) return

        wallet.isActive = false
        repository.save(wallet)
    }

    @Transactional
    override fun deleteAllByAccount(account: Account) {
        val wallets = getAllByAccount(account)
        if (wallets.isEmpty()) return

        wallets.forEach { wallet ->
            deleteByAccount(account, wallet)
        }
    }

}
