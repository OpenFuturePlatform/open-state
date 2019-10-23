package io.openfuture.state.openchain.component.openrpc

import io.openfuture.state.openchain.component.openrpc.dto.transfertransaction.TransferTransactionDto
import org.springframework.stereotype.Component

@Component
class OpenChainWrapper(
        private val openChainRPCApi: OpenChainRPCApi
) {

    fun getAllTransferTransactionDtos(offset: Long?, limit: Int): List<TransferTransactionDto> {
        return openChainRPCApi.getAllTransferTransactions(limit = limit, offset = offset ?: 0).payload.list
    }

    fun getNewTransferTransactionDtos(limit: Int, offset: Long, hash: String?): List<TransferTransactionDto> {
        val allTransferTransactionDtos = getAllTransferTransactionDtos(offset, limit)

        return allTransferTransactionDtos.takeLastWhile { it.hash != hash }
    }

    fun getBalance(walletAddress: String): Long {
        return openChainRPCApi.getWalletBalance(walletAddress).payload
    }
}