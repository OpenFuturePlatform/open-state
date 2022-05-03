package io.openfuture.state.service

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.blockchain.dto.UnifiedTransaction
import io.openfuture.state.client.BinanceHttpClientApi
import io.openfuture.state.component.open.DefaultOpenApi
import io.openfuture.state.controller.WalletController
import io.openfuture.state.domain.*
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.OrderRepository
import io.openfuture.state.repository.TransactionRepository
import io.openfuture.state.repository.WalletRepository
import io.openfuture.state.service.dto.PlaceOrderResponse
import io.openfuture.state.service.dto.WalletCreateResponse
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DefaultWalletService(
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository,
    private val webhookService: WebhookService,
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
            ?: throw NotFoundException("Wallet not found: $address")
    }

    override suspend fun findByOrderKey(orderKey: String): Wallet {
        return walletRepository.findFirstByOrder_orderKey(orderKey).awaitFirstOrNull()
            ?: throw NotFoundException("Wallet not found: $orderKey")
    }

    override suspend fun findAllByOrderKey(orderKey: String): List<Wallet> {
        return walletRepository.findAllByOrder_OrderKey(orderKey).collectList().awaitSingle()
    }

    override suspend fun findById(id: String): Wallet {
        return walletRepository.findById(id).awaitFirstOrNull()
            ?: throw NotFoundException("Wallet not found: $id")
    }

    override suspend fun save(request: WalletController.SaveWalletRequest): PlaceOrderResponse {
        val order = Order(
            request.metadata.orderId,
            request.metadata.orderKey,
            request.metadata.amount,
            request.metadata.productCurrency,
            request.metadata.source,
            request.webhook
        )
        orderRepository.save(order).awaitSingle()
        val savedWallets = mutableListOf<Wallet>()
        request.blockchains.forEach {
            val blockchain: Blockchain = blockchainLookupService.findBlockchain(it.blockchain)
            val walletIdentity = WalletIdentity(blockchain.getName(), it.address)
            val rate = binanceHttpClientApi.getExchangeRate(blockchain).price.stripTrailingZeros()
            val wallet = Wallet(walletIdentity, rate = rate, order = order)
            savedWallets.add(walletRepository.save(wallet).awaitSingle())
        }
        val wallets = savedWallets.map { WalletCreateResponse(it.identity.blockchain, it.identity.address, it.rate) }
        return PlaceOrderResponse(
            request.webhook,
            request.metadata.orderId,
            request.metadata.orderKey,
            request.metadata.amount,
            wallets
        )
    }

    override suspend fun addTransactions(blockchain: Blockchain, block: UnifiedBlock) {
        for (transaction in block.transactions) {
            val identity = WalletIdentity(blockchain.getName(), transaction.to)
            val wallet = walletRepository.findFirstByIdentity(identity)
                .awaitFirstOrNull()//walletRepository.findByIdentity(identity.blockchain, identity.address).awaitFirstOrNull()

            wallet?.let { saveTransaction(it, block, transaction) }
        }
    }

    override suspend fun updateWebhookStatus(wallet: Wallet, status: WebhookStatus) {
        //do nothing
    }

    private suspend fun saveTransaction(wallet: Wallet, block: UnifiedBlock, unifiedTransaction: UnifiedTransaction) {
        if (!transactionRepository.existsTransactionByHash(unifiedTransaction.hash)) {
            val tokens = openApi.getTokens()

            var tokenType = ""

            if (!unifiedTransaction.native)
                tokenType = tokens.firstOrNull { customToken -> customToken.address.equals(unifiedTransaction.contractAddress, ignoreCase = true) }?.symbol.toString()

            val transaction = Transaction(
                wallet.identity,
                unifiedTransaction.hash,
                unifiedTransaction.from,
                unifiedTransaction.to,
                unifiedTransaction.amount,
                block.date,
                block.number,
                block.hash,
                unifiedTransaction.native,
                tokenType
            )
            transactionRepository.save(transaction).awaitSingle()
            log.info("Saved transaction ${transaction.id}")
            val orderId = wallet.order.orderId
            val order = orderRepository.findFirstByOrderId(orderId).awaitSingle()
            if (unifiedTransaction.native) {
                val lastPaidUsd = wallet.rate.multiply(unifiedTransaction.amount)
                order.paid = order.paid.add(lastPaidUsd)
            }
            val updatedOrder = orderRepository.save(order).awaitSingle()
            webhookInvoker.invoke(wallet, transaction, updatedOrder)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(DefaultWalletService::class.java)
    }

}
