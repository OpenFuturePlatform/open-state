package io.openfuture.state.service

import io.openfuture.state.domain.WebhookDeadQueue
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.WebhookDeadQueueRepository
import io.openfuture.state.webhook.ScheduledTransaction
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service

@Service
class DefaultWebhookDeadQueueService(
        private val repository: WebhookDeadQueueRepository
): WebhookDeadQueueService {

    override suspend fun addTransactions(walletKey: String, transactions: List<ScheduledTransaction>): WebhookDeadQueue {
        val deadQueue = repository.findByWalletKey(walletKey)
                .awaitFirstOrNull() ?: WebhookDeadQueue(walletKey)

        deadQueue.addTransactions(transactions)
        return repository.save(deadQueue).awaitSingle()
    }

    override suspend fun getTransactions(walletKey: String): List<ScheduledTransaction> {
        val deadQueue = repository.findByWalletKey(walletKey)
                .awaitFirstOrNull() ?: throw NotFoundException("Wallet dead queue not found: $walletKey")

        return deadQueue.getTransactions()
    }

    override suspend fun hasTransactions(walletKey: String): Boolean {
        return repository.existsByWalletKey(walletKey).awaitSingle()
    }

    override suspend fun remove(walletKey: String) {
        repository.deleteByWalletKey(walletKey).awaitSingle()
    }
}
