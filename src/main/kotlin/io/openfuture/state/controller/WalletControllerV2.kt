package io.openfuture.state.controller

import io.openfuture.state.controller.request.BalanceRequest
import io.openfuture.state.controller.request.BlockchainRequest
import io.openfuture.state.controller.request.BroadcastRequest
import io.openfuture.state.service.BlockchainLookupService
import io.openfuture.state.service.WalletService
import io.openfuture.state.service.dto.AddWatchResponse
import io.openfuture.state.service.dto.WalletBalanceResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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

    @PostMapping("/nonce")
    suspend fun getNonce(@RequestBody request: BalanceRequest): BigInteger {

        val blockchain = walletService.getBlockchainName(request.blockchainName)
        val chain = blockchainLookupService.findBlockchain(blockchain)

        return chain.getNonce(request.address)
    }

    @PostMapping("/gas-limit")
    suspend fun getGasLimit(@RequestBody request: BlockchainRequest): BigInteger {

        val blockchain = walletService.getBlockchainName(request.blockchainName)
        val chain = blockchainLookupService.findBlockchain(blockchain)

        return chain.getGasLimit()
    }

    @PostMapping("/gas-price")
    suspend fun getGasPrice(@RequestBody request: BlockchainRequest): BigInteger {

        val blockchain = walletService.getBlockchainName(request.blockchainName)
        val chain = blockchainLookupService.findBlockchain(blockchain)

        return chain.getGasPrice()
    }

    @PostMapping("/broadcast")
    suspend fun broadcast(@RequestBody request: BroadcastRequest): String {

        val blockchain = walletService.getBlockchainName(request.blockchainName)
        val chain = blockchainLookupService.findBlockchain(blockchain)

        return chain.broadcastTransaction(request.signature)
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