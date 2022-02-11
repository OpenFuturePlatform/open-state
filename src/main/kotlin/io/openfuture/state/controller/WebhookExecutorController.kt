package io.openfuture.state.controller

import io.openfuture.state.webhook.DefaultWebhookExecutor
import io.openfuture.state.webhook.WebhookExecutor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/wallets/webhook")
class WebhookExecutorController(
    private val webhookExecutor: WebhookExecutor
) {
    @GetMapping("/{walletId}")
    suspend fun executeWebhookByWalletId(@PathVariable walletId: String) {
       webhookExecutor.testExecute(walletId)
    }
}