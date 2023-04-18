package io.openfuture.state.service

import io.openfuture.state.domain.*
import io.openfuture.state.repository.OrderRepository
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service

@Service
class DefaultWalletTransactionFacade(
    private val transactionService: DefaultTransactionService,
    private val walletService: DefaultWalletService,
    private val orderRepository: OrderRepository
) : WalletTransactionFacade {

    override suspend fun findByAddress(address: String): WalletTransactionDetail {
        val transactions = transactionService.findByAddress(address)
        val wallet = walletService.findByIdentityAddress(address)
        return WalletTransactionDetail(wallet, transactions)
    }

    override suspend fun findByOrder(orderKey: String): WalletTransactionDetail {
        val wallet = walletService.findByOrderKey(orderKey)
        val transactions = transactionService.findByAddress(wallet.identity.address)
        return WalletTransactionDetail(wallet, transactions)
    }

    override suspend fun getTransaction(address: String): List<Transaction> {
        return transactionService.findByAddress(address)
    }

    override suspend fun getOrderByApplication(applicationId: String): List<WalletPaymentDetail> {
        val orders = orderRepository.findAllByApplicationId(applicationId).collectList().awaitSingle()
        val wallets = walletService.findAllByApplication(applicationId)

        val response = mutableListOf<WalletPaymentDetail>()

        orders.forEach{ o ->
            val blockchainWallets = mutableListOf<BlockchainWallets>()
            val orderWallets = wallets.filter { w -> w.userData.order?.orderKey.equals(o.orderKey) } // every order wallets
            orderWallets.forEach { w ->
                run {
                    /*val transactions = transactionService.findByAddress(w.identity.address)
                    val sum = transactions.sumOf { w -> w.amount  }*/
                    blockchainWallets.add(BlockchainWallets(w.identity.address, w.identity.blockchain, w.userData.rate))
                }

            }
            response.add(WalletPaymentDetail(o.orderKey, o.amount, o.paid, o.productCurrency, blockchainWallets))
        }
        return response
    }
}