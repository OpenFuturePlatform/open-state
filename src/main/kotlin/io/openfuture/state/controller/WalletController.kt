package io.openfuture.state.controller

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WalletPaymentDetail
import io.openfuture.state.repository.OrderRepository
import io.openfuture.state.service.WalletService
import io.openfuture.state.service.WalletTransactionFacade
import io.openfuture.state.service.dto.PlaceOrderResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Collector
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import kotlin.streams.toList

@RestController
@RequestMapping("/api/wallets")
class WalletController(
    private val walletService: WalletService,
    private val walletTransactionFacade: WalletTransactionFacade,
    private val blockchains: List<Blockchain>
) {

    @PostMapping
    suspend fun saveMultiple(@Valid @RequestBody request: SaveOrderWalletRequest): PlaceOrderResponse {
        return walletService.saveOrder(request)
    }

    @PostMapping("/single")
    suspend fun saveSingle(@Valid @RequestBody request: SaveWalletRequest): WalletDto {
        val blockchain = findBlockchain(request.blockchain)
        val wallet = walletService.save(blockchain, request.address, request.webhook!!, request.applicationId)
        return WalletDto(wallet)
    }

    @PostMapping("/update")
    suspend fun updateSingle(@Valid @RequestBody request: UpdateOrderWalletRequest) {
        walletService.updateOrder(request)
    }

    @GetMapping("/blockchain/{blockchain}/address/{address}")
    suspend fun findByIdentity(@PathVariable blockchain: String, @PathVariable address: String): WalletDto {
        val wallet = walletService.findByIdentity(blockchain, address)
        return WalletDto(wallet)
    }

    @GetMapping("/application/{applicationId}")
    suspend fun findByApplication(@PathVariable applicationId: String): List<WalletPaymentDetail> {
        return walletTransactionFacade.getOrderByApplication(applicationId)
    }

    @DeleteMapping("/blockchain/{blockchain}/address/{address}")
    suspend fun deleteStateBynAddress(@PathVariable blockchain: String, @PathVariable address: String): Boolean {
        walletService.deleteByIdentity(blockchain, address)
        return true
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
        val applicationId: String,
        val blockchain: String,
        val lastUpdateDate: LocalDateTime,
        val nonce: Int,
        val webhook: String,
    ) {
        constructor(wallet: Wallet) : this(
            wallet.id,
            wallet.identity.address,
            wallet.applicationId,
            wallet.identity.blockchain,
            wallet.lastUpdate,
            wallet.userData.nonce,
            wallet.webhook
        )
    }

    data class SaveOrderWalletRequest(
        val applicationId: String,
        @field:NotEmpty
        val blockchains: ArrayList<BlockChainDataRequest>,
        var metadata: WalletMetaDataRequest = WalletMetaDataRequest(),
        @field:NotBlank
        val webhook: String
    )

    data class UpdateOrderWalletRequest(
        val applicationId: String,
        var metadata: WalletMetaDataRequest = WalletMetaDataRequest(),
        @field:NotBlank
        val webhook: String
    )

    data class BlockChainDataRequest(
        val address: String,
        val blockchain: String
    )

    data class SaveWalletRequest(
        @field:NotBlank
        val address: String,
        @field:NotBlank
        val applicationId: String,
        @field:NotBlank
        val blockchain: String,
        @field:NotBlank
        val webhook: String?
    )

    data class WalletMetaDataRequest(

        var amount: BigDecimal = BigDecimal.ZERO,

        @field:NotBlank
        val clientManaged: Boolean = true,

        @field:NotBlank
        var orderKey: String = UUID.randomUUID().toString(),

        @field:NotBlank
        var productCurrency: String = UUID.randomUUID().toString(),

        @field:NotBlank
        var source: String = UUID.randomUUID().toString(),

        @field:NotBlank
        val test: Boolean = true,

        var metadata: Any? = null
    )

}
