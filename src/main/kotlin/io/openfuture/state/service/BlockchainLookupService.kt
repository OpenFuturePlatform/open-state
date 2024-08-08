package io.openfuture.state.service

import io.openfuture.state.blockchain.Blockchain
import org.springframework.stereotype.Service
import java.util.*

@Service
class BlockchainLookupService(
    private val blockchains: List<Blockchain>
) {

    fun findBlockchain(name: String): Blockchain {
        val nameInLowerCase = name.lowercase()
        for (blockchain in blockchains) {
            if (blockchain.getName().lowercase().startsWith(nameInLowerCase)) return blockchain
        }

        throw IllegalArgumentException("Can not find blockchain")
    }
}