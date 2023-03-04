package io.openfuture.state.controller

import io.openfuture.state.service.WalletService
import io.openfuture.state.service.dto.AddWatchResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/wallets/v2/")
class WalletControllerV2(
    private val walletService: WalletService
) {

    @PostMapping("add")
    suspend fun addWallet(@RequestBody request: AddWalletStateRequest): AddWatchResponse {
        return walletService.addWallet(request)
    }

}

data class AddWalletStateRequest(
    val id: String,
    val webhook: String,
    val blockchains: ArrayList<BlockChain>,
    val applicationId: String,
    val metadata: MetaData
)

data class MetaData(
    var body: Any,
    var test: Boolean
)

data class BlockChain(
    val address: String,
    val blockchain: String
)