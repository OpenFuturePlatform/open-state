package io.openfuture.state.webhook

import io.openfuture.state.controller.domain.dto.TrackingTransactionDto
import io.openfuture.state.openchain.component.openrpc.dto.transfertransaction.TransferTransactionDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Component
class WebhookSender {

    fun sendWebHook(urlList: List<String>, transaction: TrackingTransactionDto) {
        send(urlList, transaction)
    }

    fun sendWebHook(urlList: List<String>, transaction: TransferTransactionDto) {
        send(urlList, transaction)
    }

    companion object {
        private val log = LoggerFactory.getLogger(WebhookSender::class.java)
    }

    private fun send(urlList: List<String>, requst: Any) {
        urlList.forEach {
            if (it.isNotEmpty()) {
                try {
                    RestTemplate().postForLocation(it, requst)
                    log.info("Sent transaction to web hook $it")
                } catch (e: RestClientException) {
                    log.warn("Error while sending to web hook $it. Cause: ${e.message}")
                }
            }
        }
    }

}
