package io.openfuture.state.webhook

import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import java.math.BigDecimal
import java.time.LocalDateTime

data class WebhookPayloadDto(
    val blockchain: String,
    val walletAddress: String,
    val transaction: WebhookTransactionDto
) {

    constructor(transaction: Transaction) : this(
        transaction.walletIdentity.blockchain,
        transaction.walletIdentity.address,
        WebhookTransactionDto(transaction)
    )

    data class WebhookTransactionDto(
        val hash: String,
        val from: Set<String>,
        val to: String,
        val amount: BigDecimal,
        val date: LocalDateTime,
        val blockHeight: Long,
        val blockHash: String
    ) {

        constructor(transaction: Transaction) : this(
            transaction.hash,
            transaction.from,
            transaction.to,
            transaction.amount,
            transaction.date,
            transaction.blockHeight,
            transaction.blockHash
        )
    }

    data class WebhookWoocommerceDto(
        val order_key: String,
        val status: String
    ) {
        constructor(wallet: Wallet, status: String) : this(
            wallet.order!!.orderKey,
            status
        )
    }

}
