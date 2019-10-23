package io.openfuture.state.service

import io.openfuture.state.component.Web3Wrapper
import io.openfuture.state.entity.Blockchain
import io.openfuture.state.entity.IntegrationType
import io.openfuture.state.openchain.component.openrpc.OpenChainWrapper
import org.springframework.stereotype.Service

@Service
class DefaultIntegrationService(
        private val web3Wrapper: Web3Wrapper,
        private val openChainWrapper: OpenChainWrapper
) : IntegrationService {

    override fun getBalance(address: String, blockchain: Blockchain): Long {
        return when (blockchain.title) {
            IntegrationType.ETHEREUM.title -> web3Wrapper.getEthBalance(address)
            IntegrationType.OPENCHAIN.title -> openChainWrapper.getBalance(address)
            else -> throw IllegalArgumentException("Not supported integration: ${blockchain.title}")
        }
    }

}
