package io.openfuture.state.service

import io.openfuture.state.domain.TransactionDto
import io.openfuture.state.entity.State
import io.openfuture.state.entity.Transaction
import io.openfuture.state.entity.Wallet
import io.openfuture.state.entity.WebHook


interface StateTrackingService {

    fun processTransaction(tx: TransactionDto)

}

interface StateService {

    fun getByWalletId(wallet: Wallet): State

    fun update(state: State)

}

interface WalletService {

    fun getByBlockchainAddress(blockchainId: Long, address: String): Wallet?

}

interface TransactionService {

    fun save(transaction: Transaction): Transaction

}

interface WebHookService {

    fun save(webHook: WebHook): WebHook

}
