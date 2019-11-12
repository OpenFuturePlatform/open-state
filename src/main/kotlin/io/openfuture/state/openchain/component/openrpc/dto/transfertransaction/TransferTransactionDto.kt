package io.openfuture.state.openchain.component.openrpc.dto.transfertransaction

class TransferTransactionDto(
        val timestamp: Long,
        val fee: Long,
        val amount: Long,
        val recipientAddress: String,
        val senderPublicKey: String,
        val senderAddress: String,
        val senderSignature: String,
        val hash: String,
        val blockHash: String,
        val data: String?,
        val status: Boolean,
        val results: List<ReceiptDto>
)