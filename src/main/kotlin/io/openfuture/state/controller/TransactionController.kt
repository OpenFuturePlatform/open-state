package io.openfuture.state.controller

import io.openfuture.state.controller.domain.dto.TrackingTransactionDto
import io.openfuture.state.controller.domain.page.PageRequest
import io.openfuture.state.controller.domain.page.PageResponse
import io.openfuture.state.service.transaction.TransactionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/wallets/{walledAddress}/transactions")
class TransactionController(
        private val transactionService: TransactionService
) {

    @GetMapping
    suspend fun getAll(@PathVariable walledAddress: String, pageRequest: PageRequest): PageResponse<TrackingTransactionDto> {
        val transactions = transactionService.getAllByWalletAddress(walledAddress, pageRequest)
        return PageResponse(transactions.map { TrackingTransactionDto(it) })
    }

    @GetMapping("/{txId}")
    suspend fun get(@PathVariable walledAddress: String, @PathVariable txId: String): TrackingTransactionDto {
        val transaction = transactionService.get(txId, walledAddress)
        return TrackingTransactionDto(transaction)
    }

}
