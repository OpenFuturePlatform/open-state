package io.openfuture.state.service

import io.openfuture.state.domain.TransactionQueueTask
import io.openfuture.state.extensions.toMillisDouble
import io.openfuture.state.repository.WalletQueueRedisRepository
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class DefaultWalletQueueService(
        private val repository: WalletQueueRedisRepository
): WalletQueueService {

    override suspend fun add(walletId: String, transaction: TransactionQueueTask) {
        repository.add(walletId, transaction, transaction.timestamp.toMillisDouble())
    }

    override suspend fun score(walletId: String): Double? {
        return repository.score(walletId).awaitSingle()
    }

    /**
     * Take only first 100 scheduled wallets, because if all of
     * them would be locked, it means that 100 instances of
     * Open State now try to process webhooks for different wallets.
     * We can't take all items, collection may contain too much
     * elements, so let assume that we never run 100 instances of
     * Open State.
     */
    override suspend fun walletsScheduledTo(timeStamp: LocalDateTime): List<String> {
        return repository
                .walletsScheduledTo(timeStamp)
                .take(100)
                .collectList()
                .awaitSingle()
    }

    override suspend fun lock(walletId: String): Boolean {
        return repository.lock(walletId)
    }

    override suspend fun unlock(walletId: String) {
        repository.unlock(walletId)
    }

    override suspend fun remove(walletId: String) {
        repository.remove(walletId)
    }

    override suspend fun incrementScore(walletId: String, diff: Double) {
        repository.incrementScore(walletId, diff)
    }
}
