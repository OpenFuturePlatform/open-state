package io.openfuture.state.service

import io.openfuture.state.domain.WalletAddress
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

    override suspend fun addTransactions(address: WalletAddress, transactions: List<ScheduledTransaction>): WebhookDeadQueue {
        val deadQueue = repository.findByWalletAddress(address)
                .awaitFirstOrNull() ?: WebhookDeadQueue(address)

        deadQueue.addTransactions(transactions)
        return repository.save(deadQueue).awaitSingle()
    }

    override suspend fun getTransactions(address: WalletAddress): List<ScheduledTransaction> {
        val deadQueue = repository.findByWalletAddress(address).awaitFirstOrNull()
                ?: throw NotFoundException("Wallet dead queue not found")

        return deadQueue.getTransactions()
    }

    override suspend fun hasTransactions(address: WalletAddress): Boolean {
        return repository.existsByWalletAddress(address).awaitSingle()
    }

    override suspend fun remove(address: WalletAddress) {
        repository.deleteByWalletAddress(address).awaitSingle()
    }
}
