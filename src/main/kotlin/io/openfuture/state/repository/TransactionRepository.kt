package io.openfuture.state.repository

import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.WalletIdentity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface TransactionRepository : ReactiveMongoRepository<Transaction, String>{
    suspend fun findAllByWalletIdentityAddress(walletIdentity_address: String): Flux<Transaction>
}
