package io.openfuture.state.webhook.dto

import io.openfuture.state.domain.Transaction

data class WebhookPayload(
        val blockchain: String,
        val walletAddress: String,
        val transaction: TransactionPayload
) {

    constructor(transaction: Transaction):
            this(transaction.walletAddress.blockchain,
                    transaction.walletAddress.address,
                    TransactionPayload(transaction)
            )
}
