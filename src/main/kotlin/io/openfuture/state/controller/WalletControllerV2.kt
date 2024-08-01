package io.openfuture.state.controller

import io.openfuture.state.controller.request.BalanceRequest
import io.openfuture.state.service.BlockchainLookupService
import io.openfuture.state.service.WalletService
import io.openfuture.state.service.dto.AddWatchResponse
import io.openfuture.state.service.dto.WalletBalanceResponse
import org.springframework.web.bind.annotation.*
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger

@RestController
@RequestMapping("/api/wallets/v2/")
class WalletControllerV2(
    private val walletService: WalletService,
    private val blockchainLookupService: BlockchainLookupService
) {

    @PostMapping("add")
    suspend fun addWallet(@RequestBody request: AddWalletStateForUserRequest): AddWatchResponse {
        return walletService.addWallet(request)
    }

    @PostMapping("/balance")
    suspend fun getBalance(@RequestBody request: BalanceRequest): WalletBalanceResponse {
        val chain = blockchainLookupService.findBlockchain(request.blockchainName)
        val balance = if (request.isNative) chain.getBalance(request.address) else chain.getContractBalance(request.address)

        return WalletBalanceResponse(blockchain = request.blockchainName, address = request.address, balance = balance, unit = Convert.Unit.ETHER.name)
    }

}

data class AddWalletStateForUserRequest(
    val webhook: String,
    val blockchains: ArrayList<BlockChain>,
    val applicationId: String,
    val userId: String,
    val test: Boolean,
    val metadata: Any?
)

data class BlockChain(
    val address: String,
    val blockchain: String
)