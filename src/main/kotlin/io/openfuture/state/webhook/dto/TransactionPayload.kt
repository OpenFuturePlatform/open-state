package io.openfuture.state.webhook.dto

import io.openfuture.state.domain.Transaction
import java.math.BigDecimal
import java.time.LocalDateTime

data class TransactionPayload(
    val hash: String,
    val from: String,
    val to: String,
    val amount: BigDecimal,
    val date: LocalDateTime,
    val blockHeight: Long,
    val blockHash: String
) {

    constructor(transaction: Transaction):
            this(transaction.hash,
                    transaction.from,
                    transaction.to,
                    transaction.amount,
                    transaction.date,
                    transaction.blockHeight,
                    transaction.blockHash
            )
}
