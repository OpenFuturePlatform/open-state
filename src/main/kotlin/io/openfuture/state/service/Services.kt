package io.openfuture.state.service

import io.openfuture.state.domain.TransactionDto
import io.openfuture.state.entity.*


interface StateTrackingService {

    fun processTransaction(tx: TransactionDto)

}

interface StateService {

    fun getByWalletId(wallet: Wallet): State

    fun save(state: State)

}

interface WalletService {

    fun create(url: String, blockchainId: Long, address: String)

    fun getByBlockchainAddress(blockchainId: Long, address: String): Wallet?

}

interface TransactionService {

    fun save(transaction: Transaction): Transaction

}

interface WebHookService {

    fun save(webHook: WebHook): WebHook

}

interface BlockchainService {

    fun get(id: Long): Blockchain

}
