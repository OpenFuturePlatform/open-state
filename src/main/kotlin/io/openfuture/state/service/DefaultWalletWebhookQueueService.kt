package io.openfuture.state.service

import io.openfuture.state.repository.WalletWebhookRedisRepository
import io.openfuture.state.util.JsonSerializer
import io.openfuture.state.webhook.ScheduledTransaction
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class DefaultWalletWebhookQueueService(
        private val repository: WalletWebhookRedisRepository,
        private val jsonSerializer: JsonSerializer
):  WalletWebhookQueueService {

    override suspend fun add(walletAddress: String, transaction: ScheduledTransaction) {
        repository.add(walletAddress, jsonSerializer.toJson(transaction), LocalDateTime.now())
    }

    override suspend fun remove(walletAddress: String) {
        repository.remove(walletAddress)
    }

    override suspend fun walletsScheduledTo(timeStamp: LocalDateTime): List<String> {
        return repository
                .walletsScheduledTo(timeStamp)
                .take(50)
                .collectList()
                .awaitSingle()
    }

    override suspend fun score(walletAddress: String): Double? {
        return repository.walletScore(walletAddress).awaitSingle()
    }

    override suspend fun incrementScore(walletAddress: String, diff: Double) {
        repository.incrementScore(walletAddress, diff)
    }

    override suspend fun lock(walletAddress: String): Boolean {
        return repository.lock(walletAddress)
    }

    override suspend fun unlock(walletAddress: String) {
        repository.unlock(walletAddress)
    }
}
