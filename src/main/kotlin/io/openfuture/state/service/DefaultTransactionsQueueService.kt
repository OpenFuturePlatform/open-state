package io.openfuture.state.service

import io.openfuture.state.domain.TransactionQueueTask
import io.openfuture.state.repository.TransactionQueueRedisRepository
import org.springframework.stereotype.Service

@Service
class DefaultTransactionsQueueService(
        private val repository: TransactionQueueRedisRepository
): TransactionsQueueService {

    override suspend fun add(walletId: String, transaction: TransactionQueueTask) {
        repository.add(walletId, transaction)
    }
}
