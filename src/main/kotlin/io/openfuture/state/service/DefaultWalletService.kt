package io.openfuture.state.service

import io.openfuture.state.entity.Wallet
import io.openfuture.state.entity.WebHook
import io.openfuture.state.repository.WalletRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultWalletService(
        private val repository: WalletRepository,
        private val webHookService: WebHookService
) : WalletService {

    @Transactional(readOnly = true)
    override fun getByBlockchainAddress(blockchainId: Long, address: String): Wallet? {
        return repository.findByBlockchainIdAndAddress(blockchainId, address)
    }

}
