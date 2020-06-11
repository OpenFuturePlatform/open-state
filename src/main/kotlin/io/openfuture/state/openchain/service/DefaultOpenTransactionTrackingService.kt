package io.openfuture.state.openchain.service

import io.openfuture.state.openchain.component.openrpc.OpenChainWrapper
import io.openfuture.state.openchain.component.openrpc.dto.transfertransaction.TransferTransactionDto
import io.openfuture.state.openchain.entity.OpenTransferTransaction
import io.openfuture.state.service.OpenScaffoldService
import io.openfuture.state.webhook.WebhookSender
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultOpenTransactionTrackingService(
        private val webHookSender: WebhookSender,
        private val openScaffoldService: OpenScaffoldService,
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
            saveAndSendTransferTransactionToWebhook(newTransactions)

            openTrackingLogService.save(offset + limit, newTransactions.last().hash)
            processTransferTransaction()
        }
    }

    private fun saveAndSendTransferTransactionToWebhook(transferTransactionDtos: List<TransferTransactionDto>) {
        transferTransactionDtos.forEach {
            it.recipientAddress.let { address ->
                {
                    openScaffoldService.findByRecipientAddress(address)?.let { scaffold ->
                        openTransferTransactionService.save(OpenTransferTransaction(it.fee,
                                it.amount,
                                it.hash,
                                it.senderAddress,
                                address,
                                it.blockHash,
                                it.timestamp,
                                scaffold.webHook))
                        webHookSender.sendWebHook(listOf(scaffold.webHook), it)
                    }
                }
            }
        }
    }

}
