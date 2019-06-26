package io.openfuture.state.controller

import io.openfuture.state.entity.Transaction
import io.openfuture.state.service.TransactionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/wallets/{walletId}/transactions")
class TransactionController(
        private val transactionService: TransactionService
) {

    @GetMapping("/wallets/{walletId}/transactions")
    fun getAllTransaction(@PathVariable walletId: Long): List<Transaction> {
        return transactionService.getAllByWalletId(walletId)
    }

    @GetMapping("/wallets/{walletId}/transactions/{txId}")
    fun getTransaction(@PathVariable walletId: Long, @PathVariable txId: Long): Transaction {
        return transactionService.get(txId, walletId)
    }

}
