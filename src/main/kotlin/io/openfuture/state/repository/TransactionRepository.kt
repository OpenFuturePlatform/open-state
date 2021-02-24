package io.openfuture.state.repository

import io.openfuture.state.domain.Transaction
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface TransactionRepository : ReactiveMongoRepository<Transaction, String> {

    fun findByHash(hash: String): Mono<Transaction>
}
