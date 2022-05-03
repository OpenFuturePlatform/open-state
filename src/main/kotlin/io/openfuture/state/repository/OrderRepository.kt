package io.openfuture.state.repository

import io.openfuture.state.domain.Order
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface OrderRepository : ReactiveMongoRepository<Order, String>{
    suspend fun findByOrderKey(orderId: String): Flux<Order>
    suspend fun findFirstByOrderId(orderId: String): Mono<Order>
}
