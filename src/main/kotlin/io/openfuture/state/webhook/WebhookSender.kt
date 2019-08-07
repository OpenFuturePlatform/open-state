package io.openfuture.state.webhook

import io.openfuture.state.controller.domain.dto.TrackingTransactionDto
import io.openfuture.state.entity.Transaction
import org.apache.commons.validator.routines.UrlValidator
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class WebhookSender {

    fun sendWebHook(urlList: List<String>, transaction: Transaction) {
        urlList.forEach {
            if (UrlValidator().isValid(it)) {
                RestTemplate().postForLocation(it, TrackingTransactionDto(transaction))
            }
        }
    }

}
