package io.openfuture.state.repository

import io.openfuture.state.domain.Order
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface OrderRepository : ReactiveMongoRepository<Order, String>{
    suspend fun findByOrderKey(orderKey: String): Mono<Order>
    suspend fun existsByOrderKey(orderKey: String): Mono<Boolean>
}
