package io.openfuture.state.openchain.component.scheduling

import io.openfuture.state.openchain.service.OpenTransactionTrackingService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class TransferTransactionsTrackingTask(
        val openTransactionTrackingService: OpenTransactionTrackingService
) {

    @Scheduled(fixedDelayString = "\${open-chain.transfer-transactions-tracking-frequency}")
    fun processTransferTransactions() {
        openTransactionTrackingService.processTransferTransaction()
    }
}