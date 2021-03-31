package io.openfuture.state.webhook

import io.openfuture.state.service.WebhookService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
    At every time process process try to get wallet
    that was scheduled to execute before current timestamp
    with minimum score in queue. If that wallet is not
    processed by another process, try to execute webhook
    and send data to specified url via HTTP POST request.
ъ=*/
@Component
class WebhookProcessor(
        private val webhookService: WebhookService,
        private val webhookExecutor: WebhookExecutor
) {

    @Scheduled(fixedDelayString = "#{@webhookProcessDelay}", initialDelay = 1000)
    fun process() = GlobalScope.launch {
        val wallets = webhookService.scheduledWallets()
        if (wallets.isEmpty()) {
            return@launch
        }

        val walletId = wallets
                .firstOrNull { webhookService.lock(it) } ?: return@launch
        log.info("Start processing webhook for wallet $walletId")

        try {
            webhookExecutor.execute(walletId)
        } catch (e: Exception) {
            log.error("Error processing webhook for wallet $walletId", e)
        }

        log.info("Webhook processed for wallet $walletId")
        webhookService.unlock(walletId)
    }

    companion object {
        private val log = LoggerFactory.getLogger(WebhookProcessor::class.java)
    }
}
