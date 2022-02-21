package io.openfuture.state.service

import io.openfuture.state.domain.WalletTransactionDetail
import org.springframework.stereotype.Service

@Service
class DefaultWalletTransactionFacade(
    private val transactionService: DefaultTransactionService,
    private val walletService: DefaultWalletService
) : WalletTransactionFacade {

    override suspend fun findByAddress(address: String): WalletTransactionDetail {
        val transactions = transactionService.findByAddress(address)
        val wallet = walletService.findByIdentityAddress(address)
        return WalletTransactionDetail(wallet, transactions)
    }

    override suspend fun findByOrder(orderKey: String): WalletTransactionDetail {
        val wallet = walletService.findByOrderKey(orderKey)
        val transactions = transactionService.findByAddress(wallet.identity.address)
        return WalletTransactionDetail(wallet, transactions)
    }
}