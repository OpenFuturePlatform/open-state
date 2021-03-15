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

    override suspend fun add(walletKey: String, transaction: ScheduledTransaction) {
        repository.add(walletKey, jsonSerializer.toJson(transaction), LocalDateTime.now())
    }

    override suspend fun remove(walletKey: String) {
        repository.remove(walletKey)
    }

    override suspend fun walletsScheduledTo(timeStamp: LocalDateTime): List<String> {
        return repository
                .walletsScheduledTo(timeStamp)
                .take(50)
                .collectList()
                .awaitSingle()
    }

    override suspend fun score(walletKey: String): Double? {
        return repository.walletScore(walletKey).awaitSingle()
    }

    override suspend fun incrementScore(walletKey: String, diff: Double) {
        repository.incrementScore(walletKey, diff)
    }

    override suspend fun lock(walletKey: String): Boolean {
        return repository.lock(walletKey)
    }

    override suspend fun unlock(walletKey: String) {
        repository.unlock(walletKey)
    }
}
