package io.openfuture.state.service

import io.openfuture.state.domain.Transaction
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.TransactionRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service

@Service
class DefaultTransactionService(
        private val repository: TransactionRepository
): TransactionService {

    override suspend fun findByHash(hash: String): Transaction {
        return repository.findByHash(hash).awaitFirstOrNull()
                ?: throw NotFoundException("Transaction not found: $hash")
    }

    override suspend fun save(transaction: Transaction): Transaction {
        return repository.save(transaction).awaitSingle()
    }
}
