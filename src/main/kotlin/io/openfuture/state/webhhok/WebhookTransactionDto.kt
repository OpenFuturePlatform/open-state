package io.openfuture.state.webhhok

import io.openfuture.state.domain.Transaction
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

}
