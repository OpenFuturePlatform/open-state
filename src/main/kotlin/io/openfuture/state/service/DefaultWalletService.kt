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
    override fun getAllByAccount(account: Account): List<Wallet> {
        return repository.findAllByAccountsContains(account)
    }

    @Transactional(readOnly = true)
    override fun get(id: Long, account: Account): Wallet {
        return repository.findByIdAndAccountsContains(id, account) ?: throw NotFoundException("Wallet with id $id not found")
    }

    @Transactional(readOnly = true)
    override fun getByBlockchainAddress(blockchainId: Long, address: String): Wallet? {
        return repository.findByBlockchainIdAndAddress(blockchainId, address)
    }

}
