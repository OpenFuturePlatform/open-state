package io.openfuture.state.webhook

import io.openfuture.state.domain.WebhookInvocation
import io.openfuture.state.repository.WebhookInvocationRedisRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

/**
    At every time when process awake it loop through list of all
    scheduled webhooks and try to lock it by wallet. If blocking
    is successful webhook would be processed.
    The main idea is to invoke webhooks for one- by one ordered by time
    of transaction date. If wallet is currently locked other processes
    wouldn't invoke webhooks on it.
ъ=*/
@Component
class WebhookInvocationProcessor(
        private val webhookRedisRepository:  WebhookInvocationRedisRepository,
        private val webhookExecutor: WebhookExecutor
) {

    @Scheduled(fixedDelayString = "#{@webhookProcessDelay}", initialDelay = 1000)
    fun process() = GlobalScope.launch {
        log.info("Start processing webhooks queue")

        webhookRedisRepository.webhooksQueue().collectMap {
            val webhook = it.value as WebhookInvocation
            execute(webhook)
        }

        log.info("Finish processing webhooks queue")
    }

    fun execute(webhookInvocation: WebhookInvocation) = GlobalScope.launch {
        if (!webhookRedisRepository.lock(webhookInvocation)) {
            return@launch
        }

        val wallet = webhookInvocation.wallet
        if (wallet.webhookStatus == WebhookStatus.FAILED || StringUtils.isEmpty(webhookInvocation.url)) {
            webhookRedisRepository.unlock(webhookInvocation)
            return@launch
        }

        log.info("Execute webhook for address {}.", webhookInvocation.address())
        webhookExecutor.execute(webhookInvocation)
        if (webhookInvocation.status == WebhookStatus.OK) {
            webhookRedisRepository.remove(webhookInvocation)
        }

        webhookRedisRepository.unlock(webhookInvocation)
        log.info("Finish executing webhook for address {}.", webhookInvocation.address())
    }

    companion object {
        private val log = LoggerFactory.getLogger(WebhookInvocationProcessor::class.java)
    }
}
