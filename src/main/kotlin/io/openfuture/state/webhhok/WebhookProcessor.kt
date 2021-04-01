package io.openfuture.state.webhhok

import io.openfuture.state.service.WebhookService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * At every time process try to get wallets scheduled
 * to execute before current moment (wallet score in
 * Redis collection need to be less than count of
 * milliseconds in current timestamp).
 * Processor starts to execute webhook for first
 * wallet tat not processed by another process
 * at the same time
*/

@Component
class WebhookProcessor(
        private val webhookService: WebhookService,
        private val webhookExecutor: WebhookExecutor
) {

    @Scheduled(fixedDelayString = "#{@webhookInvocationProcessDelay}", initialDelay = 1000)
    fun process() = GlobalScope.launch {
        val wallets = webhookService.walletsScheduledForNow()
        if (wallets.isEmpty()) {
            return@launch
        }

        val walletId = wallets.firstOrNull { webhookService.lock(it) } ?: return@launch
        log.info("Started processing webhook for wallet $walletId")

        try {
            webhookExecutor.execute(walletId)
        } catch (e: Exception) {
            log.error("Error executing webhook for wallet $walletId", e)
        }

        log.info("Webhook processed for wallet $walletId")
        webhookService.unlock(walletId)
    }

    companion object {
        private val log = LoggerFactory.getLogger(WebhookProcessor::class.java)
    }
}
