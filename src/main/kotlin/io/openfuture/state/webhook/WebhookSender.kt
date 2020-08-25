package io.openfuture.state.webhook

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Component
class WebhookSender {

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
