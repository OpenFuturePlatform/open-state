package io.openfuture.state.controller

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.controller.validation.HttpUrl
import io.openfuture.state.domain.Wallet
import io.openfuture.state.service.WalletService
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@RestController
@RequestMapping("/api/wallets")
class WalletController(private val walletService: WalletService, private val blockchains: List<Blockchain>) {

    @PostMapping
    suspend fun save(@Valid @RequestBody request: SaveWalletRequest): WalletDto {
        val blockchain = findBlockchain(request.blockchain!!)
        val wallet = walletService.save(blockchain, request)
        return WalletDto(wallet)
    }

    @PutMapping("/{walletId}")
    suspend fun update(@PathVariable walletId: String, @Valid @RequestBody request: UpdateWalletRequest): WalletDto {
        val wallet = walletService.update(walletId, request.webhook!!)
        return WalletDto(wallet)
    }

    @GetMapping("/blockchain/{blockchain}/address/{address}")
    suspend fun findByIdentity(@PathVariable blockchain: String, @PathVariable address: String): WalletDto {
        val wallet = walletService.findByIdentity(blockchain, address)
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
            wallet.identity.address,
            wallet.webhook,
            wallet.identity.blockchain,
            wallet.lastUpdate
        )
    }

    data class SaveWalletRequest(
        @field:NotBlank
        val address: String,

        @field:NotBlank
        val webhook: String,

        @field:NotBlank
        val blockchain: String?,

        var metadata: WalletMetaDataRequest = WalletMetaDataRequest()
    )

    data class WalletMetaDataRequest(
        @field:NotBlank
        var orderId: String = UUID.randomUUID().toString(),

        @field:NotBlank
        var orderKey: String = UUID.randomUUID().toString(),

        var amount: BigDecimal = BigDecimal.ZERO,

        @field:NotBlank
        var productCurrency: String = UUID.randomUUID().toString(),

        @field:NotBlank
        var source: String = UUID.randomUUID().toString(),

        @field:NotBlank
        val paymentCurrency: String = UUID.randomUUID().toString()
    )

    data class UpdateWalletRequest(
        @field:NotNull @field:NotBlank @field:HttpUrl
        val webhook: String?
    )

}
