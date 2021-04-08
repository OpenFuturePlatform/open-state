package io.openfuture.state.service

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.domain.Wallet

interface WalletService {

    suspend fun findByIdentity(blockchain: String, address: String): Wallet

    suspend fun save(blockchain: Blockchain, address: String, webhook: String): Wallet

    suspend fun addTransactions(blockchain: Blockchain, block: UnifiedBlock)
}
