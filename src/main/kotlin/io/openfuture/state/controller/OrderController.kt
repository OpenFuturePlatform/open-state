package io.openfuture.state.controller

import io.openfuture.state.domain.Wallet
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.OrderRepository
import io.openfuture.state.service.TransactionService
import io.openfuture.state.service.WalletService
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/orders")
class OrderController(
    val orderRepository: OrderRepository,
    val transactionService: TransactionService,
    val walletService: WalletService
) {

    @GetMapping("/{orderKey}")
    suspend fun getOrderState(@PathVariable orderKey: String): OrderStateResponse? {

        val wallets = walletService.findAllByOrderKey(orderKey)
        val walletsResponse: List<WalletResponse> = wallets.map { convertToWalletResponse(it) }

        return orderRepository.findByOrderKey(orderKey)
            .map { o -> OrderStateResponse(o.placedAt.plusHours(7), o.amount, o.paid, walletsResponse) }
            .switchIfEmpty(Mono.error(NotFoundException("Order with $orderKey not found")))
            .awaitSingle()

    }

    private suspend fun convertToWalletResponse(wallet: Wallet): WalletResponse {
        val transactions = transactionService.findByAddress(wallet.identity.address)
        val transactionsResponse = transactions.map {
            TransactionResponse(
                it.hash,
                it.from,
                it.to, it.amount,
                it.date,
                it.blockHeight,
                it.blockHash,
                wallet.rate,
                it.native,
                it.token
            )
        }
        return WalletResponse(wallet.identity.address, wallet.identity.blockchain, wallet.rate, transactionsResponse)
    }

}