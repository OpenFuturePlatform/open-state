package io.openfuture.state.service

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.blockchain.dto.UnifiedTransaction
import io.openfuture.state.client.BinanceHttpClientApi
import io.openfuture.state.component.open.DefaultOpenApi
import io.openfuture.state.controller.AddWalletStateForUserRequest
import io.openfuture.state.controller.WalletController
import io.openfuture.state.domain.*
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.OrderRepository
import io.openfuture.state.repository.TransactionRepository
import io.openfuture.state.repository.WalletRepository
import io.openfuture.state.service.dto.AddWatchResponse
import io.openfuture.state.service.dto.PlaceOrderResponse
import io.openfuture.state.service.dto.WalletCreateResponse
import io.openfuture.state.service.dto.WatchWalletResponse
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal

@Slf4j
@Service
class DefaultWalletService(
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository,
    private val webhookInvoker: WebhookInvoker,
    private val binanceHttpClientApi: BinanceHttpClientApi,
    private val orderRepository: OrderRepository,
    private val blockchainLookupService: BlockchainLookupService,
    private val openApi: DefaultOpenApi
) : WalletService {

    override suspend fun findByIdentity(blockchain: String, address: String): Wallet {
        val identity = WalletIdentity(blockchain, address)
        return walletRepository.findByIdentity(identity).awaitFirstOrNull()
            ?: throw NotFoundException("Wallet not found: $blockchain - $address")
    }

    override suspend fun deleteByIdentity(blockchain: String, address: String) {
        val identity = WalletIdentity(blockchain, address)
        walletRepository.deleteByIdentity(identity)
    }

    override suspend fun findByIdentityAddress(address: String): Wallet {
        return walletRepository.findFirstByIdentityAddress(address).awaitFirstOrNull()
            ?: throw NotFoundException("Wallet not found by address: $address")
    }

    override suspend fun findByOrderKey(orderKey: String): Wallet {
        return walletRepository.findFirstByOrder_orderKey(orderKey).awaitFirstOrNull()
            ?: throw NotFoundException("Wallet not found by order: $orderKey")
        return walletRepository.findFirstByUserData_Order_OrderKey(orderKey).awaitFirstOrNull()
            ?: throw NotFoundException("Wallet not found: $orderKey")
    }

    override suspend fun findAllByOrderKey(orderKey: String): List<Wallet> {
        return walletRepository.findAllByUserData_Order_OrderKey(orderKey).collectList().awaitSingle()
    }

    override suspend fun findAllByApplication(applicationId: String): List<Wallet> {
        return walletRepository.findAllByApplicationId(applicationId).collectList().awaitSingle()
    }

    override suspend fun findById(id: String): Wallet {
        return walletRepository.findById(id).awaitFirstOrNull()
            ?: throw NotFoundException("Wallet not found: $id")
    }

    override suspend fun saveOrder(request: WalletController.SaveOrderWalletRequest): PlaceOrderResponse {
        val order = Order(
            request.metadata.orderKey,
            request.applicationId,
            request.metadata.amount,
            request.metadata.productCurrency
        )
        val existOrder = orderRepository.existsByOrderKey(request.metadata.orderKey).awaitSingle()
        if (!existOrder) {
            orderRepository.save(order).awaitSingle()
        }

        val savedWallets = mutableListOf<Wallet>()
        request.blockchains.forEach {
            val blockchain: Blockchain = blockchainLookupService.findBlockchain(it.blockchain)
            val walletIdentity = WalletIdentity(blockchain.getName(), it.address)
            val rate = binanceHttpClientApi.getExchangeRate(blockchain).price.stripTrailingZeros()
            val userData = UserData(order = order, metadata = request.metadata.metadata, rate = rate)
            val wallet = Wallet(
                walletIdentity,
                request.webhook,
                applicationId = request.applicationId,
                userData = userData,
                walletType = WalletType.FOR_ORDER
            )
            savedWallets.add(walletRepository.save(wallet).awaitSingle())
        }
        val wallets = savedWallets.map { WalletCreateResponse(it.identity.blockchain, it.identity.address, it.userData.rate) }
        return PlaceOrderResponse(
            request.webhook,
            request.metadata.orderKey,
            request.metadata.amount,
            wallets
        )
    }

    //add wallet to the state for user
    override suspend fun addWallet(request: AddWalletStateForUserRequest): AddWatchResponse {
        val savedWallets = mutableListOf<Wallet>()

        request.blockchains.forEach {
            val blockchain = blockchainLookupService.findBlockchain(it.blockchain)
            val walletIdentity = WalletIdentity(blockchain.getName(), it.address.toUpperCase())
            val userData = UserData(userId = request.userId, metadata = request.metadata)
            val wallet = Wallet(walletIdentity, request.webhook, request.applicationId, userData = userData, walletType = WalletType.FOR_USER)
            val savedWallet = walletRepository.save(wallet).awaitSingle()
            savedWallets.add(savedWallet)
        }
        val wallets = savedWallets.map { WatchWalletResponse(it.identity.blockchain, it.identity.address) }
        return AddWatchResponse(request.webhook, request.userId, request.metadata, wallets)
    }

    override suspend fun updateOrder(request: WalletController.UpdateOrderWalletRequest) {
        val order = Order(
            request.metadata.orderKey,
            request.applicationId,
            request.metadata.amount,
            request.metadata.productCurrency
        )
        val existOrder = orderRepository.existsByOrderKey(request.metadata.orderKey).awaitSingle()
        println("Order exists: $existOrder")

        if (!existOrder)
            orderRepository.save(order).awaitSingle()

        val wallets = walletRepository.findAllByApplicationId(request.applicationId).collectList().awaitSingle()

        wallets.forEach { wallet ->
            wallet.userData.order = order
            walletRepository.save(wallet).awaitSingle()
        }
    }

    override suspend fun save(blockchain: Blockchain, address: String, webhook: String, applicationId: String): Wallet {
        val wallet = Wallet(WalletIdentity(blockchain.getName(), address.toUpperCase()), webhook, applicationId, userData = UserData(), walletType = WalletType.CUSTOM)
        return walletRepository.save(wallet).awaitSingle()
    }

    override suspend fun addTransactions(blockchain: Blockchain, block: UnifiedBlock) {
        for (transaction in block.transactions) {
            val identity = WalletIdentity(blockchain.getName(), transaction.to)

            val wallet = walletRepository.findByIdentity(identity).awaitFirstOrNull()//walletRepository.findByIdentity(identity.blockchain, identity.address).awaitFirstOrNull()
            val wallet = walletRepository.findByIdentity(identity).awaitFirstOrNull()

            wallet?.let { saveTransaction(it, block, transaction) }
        }
    }

    override suspend fun updateWebhookStatus(wallet: Wallet, status: WebhookStatus) {
        //do nothing
    }

    private suspend fun saveTransaction(wallet: Wallet, block: UnifiedBlock, unifiedTransaction: UnifiedTransaction) {
        log.info("Saving Transaction")
        if (!transactionRepository.existsTransactionByHash(unifiedTransaction.hash)) {

            var amount = unifiedTransaction.amount

//            var tokenType = ""
//            if (!unifiedTransaction.native) {
//                val tokens = openApi.getTokens()
//
//                val customToken = tokens.first { customToken ->
//                    customToken.address.equals(
//                        unifiedTransaction.contractAddress,
//                        ignoreCase = true
//                    )
//                }
//                tokenType = customToken.symbol
//                val result = customToken.decimal.let { 10.0.pow(it.toDouble()) }
//                amount = amount.divide(result.toBigDecimal())
//
//            }

            val transaction = Transaction(
                wallet.identity,
                unifiedTransaction.hash,
                unifiedTransaction.from,
                unifiedTransaction.to,
                amount,
                block.date,
                block.number,
                block.hash,
                unifiedTransaction.native,
                "tokenType"
            )
            transactionRepository.save(transaction).awaitSingle()
            log.info("Saved transaction ${transaction.id}")

            val nonce = wallet.userData.nonce + 1
            log.info("Wallet nonce: $nonce")
            wallet.userData.nonce = nonce
            walletRepository.save(wallet).awaitSingle()

            if (wallet.walletType == WalletType.FOR_ORDER && wallet.userData.order != null) {
                processOrder(wallet, unifiedTransaction, amount, transaction)
            } else {
                process(wallet, transaction)
            }

        }
    }

    private suspend fun process(wallet: Wallet, transaction: Transaction) {
        val metadata = wallet.userData
        val userId: String? = wallet.userData.userId
        webhookInvoker.invoke(wallet.webhook, transaction, metadata, userId)
    }

    private suspend fun processOrder(wallet: Wallet, unifiedTransaction: UnifiedTransaction, amount: BigDecimal, transaction: Transaction) {
        val orderKey = wallet.userData.order!!.orderKey
        val order = orderRepository.findByOrderKey(orderKey).awaitSingle()
        if (unifiedTransaction.native) {
            val lastPaidUsd = wallet.userData.rate.multiply(unifiedTransaction.amount)
            order.paid = order.paid.add(lastPaidUsd)
        } else {
            order.paid = order.paid.add(amount)
        }

        val updatedOrder = orderRepository.save(order).awaitSingle()
        wallet.userData.order = updatedOrder
        walletRepository.save(wallet).awaitSingle()

        webhookInvoker.invoke(wallet, transaction, updatedOrder)
    }

    companion object {
        private val log = LoggerFactory.getLogger(DefaultWalletService::class.java)
    }

}
