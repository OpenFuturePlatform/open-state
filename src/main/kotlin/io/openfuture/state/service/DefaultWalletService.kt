package io.openfuture.state.service

import io.openfuture.state.entity.Wallet
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
    override fun getAllByAccount(accountId: Long): List<Wallet> {
        return repository.findAllByAccountId(accountId)
    }

    @Transactional(readOnly = true)
    override fun get(id: Long, accountId: Long): Wallet {
        return repository.findByIdAndAccountId(id, accountId)
    }

    @Transactional(readOnly = true)
    override fun getByBlockchainAddress(blockchainId: Long, address: String): Wallet? {
        return repository.findByBlockchainIdAndAddress(blockchainId, address)
    }

}
