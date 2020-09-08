package io.openfuture.state.service

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.domain.AddTransactionRequest
import io.openfuture.state.domain.Wallet

interface WalletService {

    suspend fun save(blockchain: Blockchain, address: String, webhook: String): Wallet

    suspend fun findByAddress(address: String): Wallet

    suspend fun addTransactions(requests: List<AddTransactionRequest>)

    suspend fun existsByBlockchainAndAddress(blockchain: Blockchain, address: String): Boolean
}
