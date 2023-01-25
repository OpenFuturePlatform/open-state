package io.openfuture.state.service

import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.WalletPaymentDetail
import io.openfuture.state.domain.WalletTransactionDetail

interface WalletTransactionFacade {
    suspend fun findByAddress(address: String): WalletTransactionDetail
    suspend fun findByOrder(orderKey: String): WalletTransactionDetail
    suspend fun getTransaction(address: String): List<Transaction>
    suspend fun getOrderByApplication(applicationId: String): List<WalletPaymentDetail>
}