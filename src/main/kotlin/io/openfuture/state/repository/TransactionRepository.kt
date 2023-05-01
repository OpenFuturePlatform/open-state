package io.openfuture.state.repository

import io.openfuture.state.domain.Transaction
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface TransactionRepository : ReactiveMongoRepository<Transaction, String>{
    suspend fun findAllByWalletIdentityAddress(walletIdentityAddress: String): Flux<Transaction>
    suspend fun existsTransactionByHash(trxHash: String): Boolean
}
