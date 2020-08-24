package io.openfuture.state.service

import io.openfuture.state.controller.domain.dto.TransactionDto
import io.openfuture.state.webhook.WebhookSender
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultStateTrackingService(
        private val webHookSender: WebhookSender
) : StateTrackingService {

    @Transactional
    override suspend fun processTransaction(tx: TransactionDto) {

    }

    override suspend fun isTrackedAddress(address: String, blockchainId: Long): Boolean {
        return false
    }

}
