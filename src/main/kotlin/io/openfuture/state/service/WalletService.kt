package io.openfuture.state.service

import io.openfuture.state.domain.AddTransactionRequest
import io.openfuture.state.domain.Wallet
import io.openfuture.state.model.Blockchain

interface WalletService {

    suspend fun save(address: String, webhook: String, blockchain: Blockchain): Wallet

    suspend fun findByAddress(address: String): Wallet

    suspend fun addTransactions(requests: List<AddTransactionRequest>)

    suspend fun existsByAddressAndBlockchain(address: String, blockchain: Blockchain): Boolean
}
