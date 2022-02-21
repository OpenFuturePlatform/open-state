package io.openfuture.state.service

import io.openfuture.state.domain.WalletTransactionDetail

interface WalletTransactionFacade {
    suspend fun findByAddress(address: String): WalletTransactionDetail
    suspend fun findByOrder(orderKey: String): WalletTransactionDetail
}