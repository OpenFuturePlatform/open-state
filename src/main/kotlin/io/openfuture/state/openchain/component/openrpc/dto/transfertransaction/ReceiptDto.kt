package io.openfuture.state.openchain.component.openrpc.dto.transfertransaction

class ReceiptDto(
        val from: String,
        val to: String,
        val amount: Long,
        val data: String?,
        val error: String?
)
