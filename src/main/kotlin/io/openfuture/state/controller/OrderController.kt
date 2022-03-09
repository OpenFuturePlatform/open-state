package io.openfuture.state.controller

import io.openfuture.state.domain.Order
import io.openfuture.state.repository.OrderRepository
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class OrderController(
    val orderRepository: OrderRepository
) {

    @GetMapping("/{orderId}")
    suspend fun getOrderState(@PathVariable orderId: String): Order {
        return orderRepository.findAllByOrderId(orderId).awaitSingle()
    }

}