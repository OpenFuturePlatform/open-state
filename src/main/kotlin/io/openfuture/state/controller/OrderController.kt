package io.openfuture.state.controller

import io.openfuture.state.domain.Order
import io.openfuture.state.domain.Wallet
import io.openfuture.state.repository.OrderRepository
import io.openfuture.state.service.TransactionService
import io.openfuture.state.service.WalletService
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/orders")
class OrderController(
    val orderRepository: OrderRepository,
    val transactionService: TransactionService,
    val walletService: WalletService
) {

    @GetMapping("/{orderKey}")
    suspend fun getOrderState(@PathVariable orderKey: String): OrderStateResponse {
        val order = orderRepository.findByOrderKey(orderKey).awaitSingle()
        val wallets = walletService.findAllByOrderKey(orderKey)
        return convertToOrderStateResponse(order, wallets)
    }

    private suspend fun convertToOrderStateResponse(order: Order, wallets: List<Wallet>): OrderStateResponse {
        val orderDate = order.placedAt.plusHours(7)
        val amount = order.amount
        val paid = order.paid
        val walletsResponse: List<WalletResponse> = wallets.map { convertToWalletResponse(it) }
        return OrderStateResponse(orderDate, amount, paid, walletsResponse)
    }

    private suspend fun convertToWalletResponse(wallet: Wallet): WalletResponse {
        val transactions = transactionService.findByAddress(wallet.identity.address)
        val transactionsResponse = transactions.map {
            TransactionResponse(
                it.hash,
                it.from.first(),
                it.to, it.amount,
                it.date,
                it.blockHeight,
                it.blockHash,
                wallet.rate
            )
        }
        return WalletResponse(wallet.identity.address, wallet.identity.blockchain, transactionsResponse)
    }

}