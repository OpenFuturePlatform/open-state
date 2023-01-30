package io.openfuture.state.service

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.controller.WoocommerceController
import io.openfuture.state.controller.request.GenericWalletResponse
import io.openfuture.state.controller.request.RegisterNewWalletRequest
import io.openfuture.state.controller.request.RemoveWalletRequest
import io.openfuture.state.controller.request.UpdateWalletRequest
import io.openfuture.state.controller.response.RegisterNewWalletResponse
import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WebhookStatus
import io.openfuture.state.service.dto.PlaceOrderResponse

interface WalletService {

    suspend fun findByIdentity(blockchain: String, address: String): Wallet

    suspend fun deleteByIdentity(blockchain: String, address: String)

    suspend fun findByIdentityAddress(address: String): Wallet

    suspend fun findByOrderKey(orderKey: String): Wallet

    suspend fun findAllByOrderKey(orderKey: String): List<Wallet>

    suspend fun findById(id: String): Wallet

    suspend fun saveOrder(request: WoocommerceController.SaveOrderWalletRequest): PlaceOrderResponse

    suspend fun updateOrder(request: WoocommerceController.UpdateOrderWalletRequest)

    suspend fun save(blockchain: Blockchain, address: String, webhook: String, applicationId: String): Wallet

    suspend fun addTransactions(blockchain: Blockchain, block: UnifiedBlock)

    suspend fun updateWebhookStatus(wallet: Wallet, status: WebhookStatus)

    suspend fun register(request: RegisterNewWalletRequest): RegisterNewWalletResponse

    suspend fun remove(request: RemoveWalletRequest): GenericWalletResponse

    suspend fun update(request: UpdateWalletRequest): GenericWalletResponse

}
