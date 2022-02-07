package io.openfuture.state.service

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.controller.WalletController
import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WebhookStatus

interface WalletService {

    suspend fun findByIdentity(blockchain: String, address: String): Wallet

    suspend fun findById(id: String): Wallet

    suspend fun save(blockchain: Blockchain, request: WalletController.SaveWalletRequest): Wallet

    suspend fun addTransactions(blockchain: Blockchain, block: UnifiedBlock)

    suspend fun updateWebhookStatus(wallet: Wallet, status: WebhookStatus)

    suspend fun update(walletId: String, webhook: String): Wallet
}
