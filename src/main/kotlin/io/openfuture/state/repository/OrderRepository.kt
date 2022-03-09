package io.openfuture.state.repository

import io.openfuture.state.domain.Order
import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.WalletIdentity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface OrderRepository : ReactiveMongoRepository<Order, String>{
    suspend fun findAllByOrderId(id: String): Flux<Order>
}
