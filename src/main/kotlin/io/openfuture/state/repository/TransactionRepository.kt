package io.openfuture.state.repository

import io.openfuture.state.domain.Transaction
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionRepository : ReactiveMongoRepository<Transaction, String> {

}
