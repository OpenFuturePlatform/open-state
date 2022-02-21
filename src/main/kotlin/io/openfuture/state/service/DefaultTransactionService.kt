package io.openfuture.state.service

import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.WalletIdentity
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.TransactionRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

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

}
