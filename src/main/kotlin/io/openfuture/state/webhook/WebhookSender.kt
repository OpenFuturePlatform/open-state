package io.openfuture.state.webhook

import io.openfuture.state.controller.domain.dto.TrackingTransactionDto
import io.openfuture.state.entity.Transaction
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class WebhookSender {

    fun sendWebHook(urlList: List<String>, transaction: Transaction) {
        urlList.forEach {
            if (it.isNotEmpty()) {
                RestTemplate().postForLocation(it, TrackingTransactionDto(transaction))
            }
        }
    }

}
