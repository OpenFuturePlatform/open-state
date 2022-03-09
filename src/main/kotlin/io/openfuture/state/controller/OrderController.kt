package io.openfuture.state.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class OrderController {

    @GetMapping("/{orderId}")
    fun getOrderState(@PathVariable orderId: String) {

    }

}