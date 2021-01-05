package io.openfuture.state.webhook

import io.openfuture.state.config.WebhookConfig
import io.openfuture.state.domain.WebhookInvocation
import io.openfuture.state.util.MathUtils
import kotlinx.coroutines.delay
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class DefaultWebhookExecutor(
        private val webhookConfig: WebhookConfig
): WebhookExecutor {

    override suspend fun execute(webhookInvocation: WebhookInvocation) {
        try {
            for (i in 1..webhookConfig.maxAttempts()) {

                webhookInvocation.attempts++
                if (executeGetRequest(webhookInvocation.url)) {
                    webhookInvocation.status = WebhookStatus.OK
                    return
                }

                delay(1000 * MathUtils.fibonachi(i))
            }
        } catch (ex: Exception) {
            webhookInvocation.message = ex.message
        }

        webhookInvocation.apply {
            status = WebhookStatus.FAILED
            wallet.webhookStatus = WebhookStatus.FAILED
        }
    }

    suspend fun executeGetRequest(url: String): Boolean {
        val restTemplate = RestTemplate()
        val response = restTemplate.getForEntity(url, String::class.java)

        return response.statusCode.is2xxSuccessful
    }
}
