package io.openfuture.state.controller

import io.openfuture.state.domain.Transaction
import io.openfuture.state.service.DefaultTransactionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/api/wallets/transactions")
class TransactionController(private val transactionService: DefaultTransactionService) {

    @GetMapping("/address/{address}")
    suspend fun findByIdentity(@PathVariable address: String): List<Transaction> {
        return transactionService.findByAddress(address)
    }
}