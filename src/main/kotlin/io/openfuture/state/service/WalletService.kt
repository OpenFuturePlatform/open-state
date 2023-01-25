package io.openfuture.state.service

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.controller.WalletController
import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WebhookStatus
import io.openfuture.state.service.dto.PlaceOrderResponse
import reactor.core.publisher.Flux

interface WalletService {

    suspend fun findByIdentity(blockchain: String, address: String): Wallet

    suspend fun deleteByIdentity(blockchain: String, address: String)

    suspend fun findByIdentityAddress(address: String): Wallet

    suspend fun findByOrderKey(orderKey: String): Wallet

    suspend fun findAllByOrderKey(orderKey: String): List<Wallet>

    suspend fun findAllByApplication(applicationId: String): List<Wallet>

    suspend fun findById(id: String): Wallet

    suspend fun saveOrder(request: WalletController.SaveOrderWalletRequest): PlaceOrderResponse

    suspend fun updateOrder(request: WalletController.UpdateOrderWalletRequest)

    suspend fun save(blockchain: Blockchain, address: String, webhook: String, applicationId: String): Wallet

    suspend fun addTransactions(blockchain: Blockchain, block: UnifiedBlock)

    suspend fun updateWebhookStatus(wallet: Wallet, status: WebhookStatus)

}
