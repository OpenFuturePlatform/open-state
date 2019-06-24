package io.openfuture.state.service

import io.openfuture.state.entity.Wallet
import io.openfuture.state.repository.WalletRepository
import org.springframework.stereotype.Service

@Service
class DefaultWalletService(
        private val repository: WalletRepository
) : WalletService {

    override fun getByBlockchainAddress(blockchainId: Long, address: String): Wallet? {
        return repository.findByBlockchainIdAndAddress(blockchainId, address)
    }

}
