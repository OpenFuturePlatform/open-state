package io.openfuture.state.domain

import java.math.BigDecimal

data class WalletTransactionDetail(
    var orderKey: String,
    var amount: BigDecimal,
    var totalPaid: BigDecimal = BigDecimal.ZERO,
    var rate: BigDecimal,
    val transactions: List<Transaction>
)
{
    constructor(wallet: Wallet, transactions: List<Transaction>) : this(
        wallet.userData.order!!.orderKey, wallet.userData.order!!.amount, wallet.userData.order!!.paid, wallet.userData.rate, transactions
    )
}
