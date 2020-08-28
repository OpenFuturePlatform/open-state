package io.openfuture.state.controller

import io.openfuture.state.domain.Wallet
import io.openfuture.state.service.WalletService
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@RestController
@RequestMapping("/api/wallets")
class WalletController(private val walletService: WalletService) {

    // TODO: Needs to be secured and allowed only for Open API calls
    @PostMapping
    suspend fun save(@Valid @RequestBody request: SaveWalletRequest): WalletDto {
        val wallet = walletService.save(request.address, request.webhook)
        return WalletDto(wallet)
    }

    // TODO: Open Developer should also pass his/her token for the authentication.
    @GetMapping("/address/{address}")
    suspend fun findByAddress(@PathVariable address: String): WalletDto {
        val wallet = walletService.findByAddress(address)
        return WalletDto(wallet)
    }

    data class WalletDto(
            val id: String,
            val address: String,
            val webhook: String,
            val lastUpdateDate: LocalDateTime
    ) {
        constructor(wallet: Wallet) : this(
                wallet.id.toHexString(),
                wallet.address,
                wallet.webhook,
                wallet.lastUpdate
        )
    }

    data class SaveWalletRequest(
            @field:NotNull @field:NotBlank val address: String,
            @field:NotNull @field:NotBlank val webhook: String,
    )
}
