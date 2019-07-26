package io.openfuture.state.controller

import io.openfuture.state.controller.domain.dto.TransactionDto
import io.openfuture.state.service.StateTrackingService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class TrackingController(
        private val stateTrackingService: StateTrackingService
) {

    @PostMapping("/transactions")
    fun processTransaction(@RequestBody tx: TransactionDto) {
        stateTrackingService.processTransaction(tx)
    }

}
