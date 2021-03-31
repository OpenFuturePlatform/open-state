package io.openfuture.state.controller

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.domain.Wallet
import io.openfuture.state.service.WalletService
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@RestController
@RequestMapping("/api/wallets")
class WalletController(private val walletService: WalletService, private val blockchains: List<Blockchain>) {

    // TODO: Needs to be secured and allowed only for Open API calls
    @PostMapping
    suspend fun save(@Valid @RequestBody request: SaveWalletRequest): WalletDto {
        val blockchain = findBlockchain(request.blockchain!!)
        val wallet = walletService.save(blockchain, request.address!!, request.webhook!!)
        return WalletDto(wallet)
    }

    // TODO: Open Developer should also pass his/her token for the authentication.
    @GetMapping("/blockchain/{blockchain}/address/{address}")
    suspend fun findByAddress(@PathVariable blockchain: String, @PathVariable address: String): WalletDto {
        val wallet = walletService.findByAddress(blockchain, address)
        return WalletDto(wallet)
    }

    private fun findBlockchain(name: String): Blockchain {
        val nameInLowerCase = name.toLowerCase()
        for (blockchain in blockchains) {
            if (blockchain.getName().toLowerCase().startsWith(nameInLowerCase)) return blockchain
        }

        throw IllegalArgumentException("Can not find blockchain")
    }

    data class WalletDto(
            val id: String,
            val address: String,
            val webhook: String,
            val blockchain: String,
            val lastUpdateDate: LocalDateTime
    ) {
        constructor(wallet: Wallet) : this(
                wallet.id,
                wallet.address.address,
                wallet.webhook,
                wallet.address.blockchain,
                wallet.lastUpdate
        )
    }

    data class SaveWalletRequest(
            @field:NotNull @field:NotBlank val address: String?,
            @field:NotNull @field:NotBlank val webhook: String?,
            @field:NotNull val blockchain: String?
    )
}
