package io.openfuture.state.controller

import io.openfuture.state.controller.domain.dto.WalletDto
import io.openfuture.state.controller.domain.request.SaveWalletRequest
import io.openfuture.state.service.WalletService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/wallets")
class WalletController(private val walletService: WalletService) {

    @PostMapping
    suspend fun save(@RequestBody request: SaveWalletRequest): WalletDto {
        return walletService.save(request)
    }

    @GetMapping("/address/{address}")
    suspend fun findByAddress(@PathVariable address: String): WalletDto {
        return walletService.findByAddress(address)
    }

}
