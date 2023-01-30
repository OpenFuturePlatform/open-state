package io.openfuture.state.controller.response

import java.math.BigDecimal

class RegisterNewWalletResponse(
    val status: String,
    val webhook: String,
    val wallets: List<BlockchainResponse>
)

class BlockchainResponse(
    val blockchain: String,
    val address: String,
    val rate: BigDecimal
)