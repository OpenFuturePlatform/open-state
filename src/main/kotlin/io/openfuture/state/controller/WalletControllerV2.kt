package io.openfuture.state.controller

import io.openfuture.state.config.AppProperties
import io.openfuture.state.controller.request.BalanceRequest
import io.openfuture.state.service.BlockchainLookupService
import io.openfuture.state.service.WalletService
import io.openfuture.state.service.dto.AddWatchResponse
import io.openfuture.state.service.dto.WalletBalanceResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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

        // val CONTRACT_ADDRESS = "0x1c7D4B196Cb0C7B01d743Fbc6116a902379C7238" // USDC - ETH
        // val CONTRACT_ADDRESS = "0x337610d27c682E347C9cD60BD4b3b107C9d34dDd" // USDT - BNB
        // val CONTRACT_ADDRESS = "0xdAC17F958D2ee523a2206206994597C13D831ec7" // USDT - TRX

        val blockchain = walletService.getBlockchainName(request.blockchainName)
        val chain = blockchainLookupService.findBlockchain(blockchain)

        val balance =
            if (request.contractAddress == null)
                chain.getBalance(request.address)
            else
                chain.getContractBalance(request.address, request.contractAddress)
        println("Balance response for address: ${request.address} is $balance")

        return WalletBalanceResponse(
            blockchain = request.blockchainName,
            address = request.address,
            balance = balance
        )
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