package io.openfuture.state.service

import io.openfuture.state.repository.WalletWebhookRedisRepository
import io.openfuture.state.webhook.ScheduledTransaction
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class DefaultWalletQueueService(
        private val repository: WalletWebhookRedisRepository
):  WalletQueueService {

    override suspend fun add(walletId: String, transaction: ScheduledTransaction) {
        repository.add(walletId, transaction, LocalDateTime.now())
    }

    override suspend fun remove(walletId: String) {
        repository.remove(walletId)
    }

    override suspend fun walletsScheduledTo(timeStamp: LocalDateTime): List<String> {
        return repository
                .walletsScheduledTo(timeStamp)
                .take(50)
                .collectList()
                .awaitSingle()
    }

    override suspend fun score(walletId: String): Double? {
        return repository.walletScore(walletId).awaitSingle()
    }

    override suspend fun incrementScore(walletId: String, diff: Double) {
        repository.incrementScore(walletId, diff)
    }

    override suspend fun lock(walletId: String): Boolean {
        return repository.lock(walletId)
    }

    override suspend fun unlock(walletId: String) {
        repository.unlock(walletId)
    }
}
