package io.openfuture.state.controller

import io.openfuture.state.controller.domain.dto.TrackingTransactionDto
import io.openfuture.state.controller.domain.page.PageRequest
import io.openfuture.state.controller.domain.page.PageResponse
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

    @GetMapping
    fun getAllTransaction(@PathVariable walletId: Long, pageRequest: PageRequest): PageResponse<TrackingTransactionDto> {
        val transactions = transactionService.getAllByWalletId(walletId, pageRequest)
        return PageResponse(transactions.map { TrackingTransactionDto(it) })
    }

    @GetMapping("/{txId}")
    fun getTransaction(@PathVariable walletId: Long, @PathVariable txId: Long): TrackingTransactionDto {
        val transaction = transactionService.get(txId, walletId)
        return TrackingTransactionDto(transaction)
    }

}
