package io.openfuture.state.controller

import io.openfuture.state.domain.WalletTransactionDetail
import io.openfuture.state.service.WalletTransactionFacade
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/wallets/transactions")
class TransactionController(private val walletTransactionFacade: WalletTransactionFacade) {

    @GetMapping("/address/{address}")
    suspend fun findByIdentity(@PathVariable address: String): WalletTransactionDetail {
        return walletTransactionFacade.findByAddress(address)
    }

    @GetMapping("/order/{orderKey}")
    suspend fun findByOrder(@PathVariable orderKey: String): WalletTransactionDetail {
        return walletTransactionFacade.findByOrder(orderKey)
    }
}