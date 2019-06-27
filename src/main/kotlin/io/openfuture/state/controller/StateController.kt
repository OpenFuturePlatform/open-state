package io.openfuture.state.controller

import io.openfuture.state.entity.State
import io.openfuture.state.service.StateService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/wallets/{walletId}/states")
class StateController(
        private val stateService: StateService
) {

    @GetMapping
    fun getState(@PathVariable walletId: Long): State {
        return stateService.getByWalletId(walletId)
    }

}
