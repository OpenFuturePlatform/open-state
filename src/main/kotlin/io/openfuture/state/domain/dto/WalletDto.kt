package io.openfuture.state.domain.dto

import io.openfuture.state.entity.Wallet
import java.time.LocalDateTime

data class WalletDto(
        val id: Long,
        val address: String,
        val balance: Double,
        val currency: String,
        val lastUpdated: Long,
        val startTrackingDate: Long

) {

    constructor(wallet: Wallet) : this(
            wallet.id,
            wallet.address,
            wallet.state.balance.div(10.pow(wallet.blockchain.coin.decimals)),
            wallet.blockchain.coin.short_title,
            wallet.state.date,
            wallet.startTrackingDate
    )

}

fun Int.pow(var1: Int): Double = Math.pow(this.toDouble(), var1.toDouble())
