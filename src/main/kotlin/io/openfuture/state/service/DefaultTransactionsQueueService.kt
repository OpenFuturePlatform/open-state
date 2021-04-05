package io.openfuture.state.service

import io.openfuture.state.domain.TransactionQueueTask
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.TransactionQueueRedisRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Service

@Service
class DefaultTransactionsQueueService(
        private val repository: TransactionQueueRedisRepository
): TransactionsQueueService {

    override suspend fun add(walletId: String, transaction: TransactionQueueTask) {
        repository.add(walletId, transaction)
    }

    override suspend fun first(walletId: String): TransactionQueueTask {
        return repository.first(walletId)
                .awaitFirstOrNull()
                ?: throw NotFoundException("Transaction not found")
    }
}
