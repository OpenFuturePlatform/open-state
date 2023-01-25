package io.openfuture.state.domain

import java.math.BigDecimal

data class WalletPaymentDetail(
    var orderKey: String,
    var amount: BigDecimal,
    var totalPaid: BigDecimal = BigDecimal.ZERO,
    var currency: String,
    val blockchains: List<BlockchainWallets>
)

data class BlockchainWallets(
    val address: String,
    val blockchain: String,
    val rate: BigDecimal,
    val totalValue: BigDecimal
)
