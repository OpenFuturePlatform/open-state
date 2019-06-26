package io.openfuture.state.service

import io.openfuture.state.domain.request.CreateIntegrationRequest
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

    override fun create(url: String, integrations: Set<CreateIntegrationRequest>) {
        val webHook = webHookService.save(WebHook(url))

        integrations.forEach {
            val blockchain = blockchainService.get(it.blockchainId)
            val wallet = repository.save(Wallet(webHook, blockchain, it.address))
            stateService.save(State(wallet, root = "start root"))
        }
    }

    @Transactional(readOnly = true)
    override fun getByBlockchainAddress(blockchainId: Long, address: String): Wallet? {
        return repository.findByBlockchainIdAndAddress(blockchainId, address)
    }

}
