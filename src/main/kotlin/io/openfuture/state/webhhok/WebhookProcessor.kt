package io.openfuture.state.webhhok

import io.openfuture.state.service.WebhookService
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * At every time process try to get wallet scheduled
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
    fun process() = runBlocking {
        var walletTask = webhookService.firstWalletInQueue() ?: return@runBlocking
        while (!webhookService.lock(walletTask.walletId)) {
            walletTask = webhookService.firstWalletInQueue(walletTask.score) ?: return@runBlocking
        }


        log.info("Start process webhook for wallet $walletTask.walletId")

        try {
            webhookExecutor.execute(walletTask.walletId)
        } catch (e: Exception) {
            log.error("Error executing webhook for wallet $walletTask.walletId", e)
        }

        log.info("Webhook process for wallet $walletTask.walletId completed")
        webhookService.unlock(walletTask.walletId)
    }

    companion object {
        private val log = LoggerFactory.getLogger(WebhookProcessor::class.java)
    }

}
