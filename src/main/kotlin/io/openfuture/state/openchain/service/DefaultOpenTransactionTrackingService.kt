package io.openfuture.state.openchain.service

import io.openfuture.state.entity.OpenScaffold
import io.openfuture.state.openchain.component.openrpc.OpenChainWrapper
import io.openfuture.state.openchain.component.openrpc.dto.transfertransaction.TransferTransactionDto
import io.openfuture.state.openchain.entity.OpenTransferTransaction
import io.openfuture.state.service.OpenScaffoldService
import io.openfuture.state.webhook.WebhookSender
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.coroutines.suspendCoroutine

@Service
class DefaultOpenTransactionTrackingService(
        private val webHookSender: WebhookSender,
        private val openScaffoldService: OpenScaffoldService,
        private val openChainWrapper: OpenChainWrapper,
        private val openTrackingLogService: OpenTrackingLogService,
        private val openTransferTransactionService: OpenTransferTransactionService
) : OpenTransactionTrackingService {

    @Transactional
    override tailrec suspend fun processTransferTransaction() {
        val lastOpenTrackingLog = openTrackingLogService.getLastOpenTrackingLog()
        val limit = 10
        val offset = lastOpenTrackingLog?.offset ?: 0

        val newTransactions = openChainWrapper.getNewTransferTransactionDtos(limit, offset, lastOpenTrackingLog?.hash)

        if (newTransactions.isNotEmpty()) {
            saveAndSendTransferTransactionToWebhook(newTransactions)

            openTrackingLogService.save(offset = offset + limit, hash = newTransactions.last().hash)
            processTransferTransaction()
        }
    }

    private suspend fun saveAndSendTransferTransactionToWebhook(transferTransactionDtos: List<TransferTransactionDto>) {
        transferTransactionDtos.forEach {
            it.recipientAddress.let { address ->
                {
                    suspend {
                        openScaffoldService.findByRecipientAddress(address)?.let { scaffold ->
                            suspend {
                                openTransferTransactionService.save(OpenTransferTransaction(fee = it.fee,
                                        amount = it.amount,
                                        hash = it.hash,
                                        senderAddress = it.senderAddress,
                                        recipientAddress = address,
                                        blockHash = it.blockHash,
                                        date = it.timestamp,
                                        webHook = scaffold.webHook))
                            }
                            webHookSender.sendWebHook(listOf(scaffold.webHook), it)
                        }
                    }
                }
            }
        }

    }

}
