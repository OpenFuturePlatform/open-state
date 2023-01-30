package io.openfuture.state.controller

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.controller.request.GenericWalletResponse
import io.openfuture.state.controller.request.RegisterNewWalletRequest
import io.openfuture.state.controller.request.RemoveWalletRequest
import io.openfuture.state.controller.request.UpdateWalletRequest
import io.openfuture.state.controller.response.RegisterNewWalletResponse
import io.openfuture.state.service.WalletService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/wallets/v2")
class WalletController(
    private val walletService: WalletService,
    private val blockchains: List<Blockchain>
) {

    @PostMapping("/register")
    suspend fun register(@Valid @RequestBody request: RegisterNewWalletRequest): RegisterNewWalletResponse {
        return walletService.register(request)
    }

    @PutMapping("/update")
    suspend fun update(@Valid @RequestBody request: UpdateWalletRequest): GenericWalletResponse {
        return walletService.update(request)
    }

    @PutMapping("/remove")
    suspend fun remove(@Valid @RequestBody request: RemoveWalletRequest): GenericWalletResponse {
        return walletService.remove(request)
    }

}