package io.openfuture.state.service

import io.openfuture.state.component.Web3Wrapper
import io.openfuture.state.entity.Blockchain
import io.openfuture.state.entity.IntegrationType
import io.openfuture.state.exception.NotFoundException
import org.springframework.stereotype.Service

@Service
class DefaultIntegrationService(
        private val web3Wrapper: Web3Wrapper
) : IntegrationService {

    override fun getBalance(address: String, blockchain: Blockchain): Long {
        return when (blockchain.title) {
            IntegrationType.ETHEREUM.title -> web3Wrapper.getEthBalance(address)
            else -> throw NotFoundException("Not supported integration: ${blockchain.title}")
        }
    }

}
