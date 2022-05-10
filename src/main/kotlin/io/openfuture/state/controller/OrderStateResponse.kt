package io.openfuture.state.controller

import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderStateResponse(
    val orderDate: LocalDateTime,
    val orderAmount: BigDecimal,
    val paid: BigDecimal,
    val wallets: List<WalletResponse>
)

data class WalletResponse(
    val address: String,
    val blockchain: String,
    val rate: BigDecimal,
    val transactions: List<TransactionResponse>
)

data class TransactionResponse(
    val hash: String,
    val from: Set<String>,
    val to: String,
    val amount: BigDecimal,
    val date: LocalDateTime,
    val blockHeight: Long,
    val blockHash: String,
    val rate: BigDecimal,
    val native: Boolean?,
    val token: String?
)