package io.openfuture.state.service

import io.openfuture.state.entity.State
import io.openfuture.state.entity.Wallet
import io.openfuture.state.entity.WebHook
import io.openfuture.state.repository.WalletRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultWalletService(
        private val repository: WalletRepository,
        private val stateService: StateService,
        private val webHookService: WebHookService,
        private val blockchainService: BlockchainService
) : WalletService {

    override fun create(url: String, blockchainId: Long, address: String) {
        val webHook = webHookService.save(WebHook("url"))

        val blockchain = blockchainService.get(blockchainId)

        val wallet = Wallet(webHook, blockchain, address)
        repository.save(wallet)
        stateService.save(State(wallet, root = "start root"))
    }

    @Transactional(readOnly = true)
    override fun getByBlockchainAddress(blockchainId: Long, address: String): Wallet? {
        return repository.findByBlockchainIdAndAddress(blockchainId, address)
    }

}
