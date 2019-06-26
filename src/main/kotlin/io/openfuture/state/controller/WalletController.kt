package io.openfuture.state.controller

import io.openfuture.state.domain.request.CreateWalletRequest
import io.openfuture.state.service.WalletService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/wallets")
class WalletController(
        private val walletService: WalletService
) {

    @PostMapping
    fun createWallet(@RequestBody request: CreateWalletRequest) {
        walletService.create(request.webHook, request.integrations)
    }

}
