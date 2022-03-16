package io.openfuture.state.service

import io.openfuture.state.blockchain.Blockchain
import org.springframework.stereotype.Service

@Service
class BlockchainLookupService (
    private val blockchains: List<Blockchain>
        ){

    fun findBlockchain(name: String): Blockchain {
        val nameInLowerCase = name.toLowerCase()
        for (blockchain in blockchains) {
            if (blockchain.getName().toLowerCase().startsWith(nameInLowerCase)) return blockchain
        }

        throw IllegalArgumentException("Can not find blockchain")
    }
}