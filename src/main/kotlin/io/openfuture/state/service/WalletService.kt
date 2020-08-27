package io.openfuture.state.service

import io.openfuture.state.domain.Wallet

interface WalletService {

    suspend fun save(address: String, webhook: String): Wallet

    suspend fun findByAddress(address: String): Wallet
}
