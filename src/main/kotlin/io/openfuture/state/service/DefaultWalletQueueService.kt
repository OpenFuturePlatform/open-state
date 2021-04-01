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
        repository.add(walletId, transaction, LocalDateTime.now().toMillisDouble())
    }

    override suspend fun score(walletId: String): Double? {
        return repository.score(walletId).awaitSingle()
    }
}
