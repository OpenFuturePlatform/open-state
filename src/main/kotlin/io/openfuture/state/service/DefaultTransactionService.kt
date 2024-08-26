package io.openfuture.state.service

import io.openfuture.state.domain.transaction.Transaction
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.TransactionRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service

@Service
class DefaultTransactionService(
    private val repository: TransactionRepository
) : TransactionService {

    override suspend fun findById(id: String): Transaction {
        return repository.findById(id).awaitFirstOrNull()
            ?: throw NotFoundException("Transaction not found: $id")
    }

    override suspend fun findByAddress(address: String): List<Transaction> {
        return repository.findAllByWalletIdentityAddress(address).collectList().awaitFirstOrNull()
            ?: throw NotFoundException("Transaction not found : $address")
    }

    override suspend fun findAll(): List<Transaction> {
        return repository.findAll().collectList().awaitSingle()
    }

}
