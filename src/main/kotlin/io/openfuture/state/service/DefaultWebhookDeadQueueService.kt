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

    override suspend fun addTransactions(walletAddress: String, transactions: List<ScheduledTransaction>): WebhookDeadQueue {
        val deadQueue = repository.findByWalletAddress(walletAddress)
                .awaitFirstOrNull() ?: WebhookDeadQueue(walletAddress)

        deadQueue.addTransactions(transactions)
        return repository.save(deadQueue).awaitSingle()
    }

    override suspend fun getTransactions(walletAddress: String): List<ScheduledTransaction> {
        val deadQueue = repository.findByWalletAddress(walletAddress)
                .awaitFirstOrNull() ?: throw NotFoundException("Wallet dead queue not found: $walletAddress")

        return deadQueue.getTransactions()
    }

    override suspend fun hasTransactions(walletAddress: String): Boolean {
        return repository.existsByWalletAddress(walletAddress).awaitSingle()
    }

    override suspend fun remove(walletAddress: String) {
        repository.deleteByWalletAddress(walletAddress).awaitSingle()
    }
}
