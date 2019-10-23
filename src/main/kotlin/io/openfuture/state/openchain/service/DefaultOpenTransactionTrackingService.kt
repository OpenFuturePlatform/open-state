package io.openfuture.state.openchain.service

import io.openfuture.state.openchain.component.openrpc.OpenChainWrapper
import io.openfuture.state.openchain.component.openrpc.dto.transfertransaction.TransferTransactionDto
import io.openfuture.state.openchain.dto.OpenTransferTransactionDto
import io.openfuture.state.openchain.entity.OpenTransferTransaction
import io.openfuture.state.webhook.WebhookSender
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultOpenTransactionTrackingService(
        private val webHookSender: WebhookSender,
        private val openChainWrapper: OpenChainWrapper,
        private val openTrackingLogService: OpenTrackingLogService,
        private val openTransferTransactionService: OpenTransferTransactionService
) : OpenTransactionTrackingService {

    @Transactional
    override tailrec fun processTransferTransaction() {
        val lastOpenTrackingLog = openTrackingLogService.getLastOpenTrackingLog()
        val limit = 10
        val offset = lastOpenTrackingLog?.offset ?: 0

        val newTransactions = openChainWrapper.getNewTransferTransactionDtos(limit, offset, lastOpenTrackingLog?.hash)

        if (newTransactions.isNotEmpty()) {
            val trackedTransferTransactions = getTrackedTransferTransactions(newTransactions)
            sendTransferTransactionToWebhook(trackedTransferTransactions)

            openTrackingLogService.save(offset + limit, newTransactions.last().hash)
            processTransferTransaction()
        }
    }

    private fun sendTransferTransactionToWebhook(trackedTransferTransactions: List<OpenTransferTransaction>?) {
        if (trackedTransferTransactions.isNullOrEmpty()) return

        trackedTransferTransactions.forEach {
            webHookSender.sendWebHook(listOf(it.webHook),
                    OpenTransferTransactionDto(it.id,
                            it.hash,
                            it.amount,
                            it.fee,
                            it.date,
                            it.blockHash,
                            it.senderAddress,
                            it.recipientAddress)
            )
        }
    }

    private fun getTrackedTransferTransactions(transferTransactionDtos: List<TransferTransactionDto>)
            : List<OpenTransferTransaction>? {
        val hashes = transferTransactionDtos.map { it.hash }
        return openTransferTransactionService.findByHashes(hashes)
    }

}
