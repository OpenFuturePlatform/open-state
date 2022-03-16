package io.openfuture.state.controller

import io.openfuture.state.controller.request.ManualTransactionRequest
import io.openfuture.state.domain.WalletTransactionDetail
import io.openfuture.state.service.BlockchainLookupService
import io.openfuture.state.service.DefaultWalletService
import io.openfuture.state.service.WalletTransactionFacade
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/wallets/transactions")
class TransactionController(
    private val walletTransactionFacade: WalletTransactionFacade,
    private val walletService: DefaultWalletService,
    private val blockchainLookupService: BlockchainLookupService
) {

    @GetMapping("/address/{address}")
    suspend fun findByIdentity(@PathVariable address: String): WalletTransactionDetail {
        return walletTransactionFacade.findByAddress(address)
    }

    @GetMapping("/order/{orderKey}")
    suspend fun findByOrder(@PathVariable orderKey: String): WalletTransactionDetail {
        return walletTransactionFacade.findByOrder(orderKey)
    }

    @PostMapping("/admin/sendManualTransaction")
    suspend fun sendManualTransaction(@RequestBody request: ManualTransactionRequest) {
        val blockchain = blockchainLookupService.findBlockchain(request.blockchainName)
        walletService.addTransactions(blockchain, request.unifiedBlock)
    }

}