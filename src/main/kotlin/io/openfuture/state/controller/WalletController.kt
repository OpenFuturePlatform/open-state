package io.openfuture.state.controller

import io.openfuture.state.domain.Wallet
import io.openfuture.state.service.WalletService
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@RequestMapping("/api/wallets")
class WalletController(private val walletService: WalletService) {

    @PostMapping
    suspend fun save(@Valid @RequestBody request: SaveWalletRequest) {
        walletService.save(request)
    }

    @GetMapping("/blockchain/{blockchain}/address/{address}")
    suspend fun findByIdentity(@PathVariable blockchain: String, @PathVariable address: String): WalletDto {
        val wallet = walletService.findByIdentity(blockchain, address)
        return WalletDto(wallet)
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
            "",
            wallet.identity.blockchain,
            wallet.lastUpdate,
        )
    }

    data class SaveWalletRequest(
        @field:NotBlank
        val webhook: String,
        val blockchainData: ArrayList<BlockChainDataRequest>,
        var metadata: WalletMetaDataRequest = WalletMetaDataRequest()
    )

    data class BlockChainDataRequest(
        val address: String,
        val blockchain: String
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

}
