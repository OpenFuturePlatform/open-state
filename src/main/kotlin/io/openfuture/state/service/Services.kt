package io.openfuture.state.service

import io.openfuture.state.domain.dto.TransactionDto
import io.openfuture.state.domain.request.CreateIntegrationRequest
import io.openfuture.state.entity.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable


interface StateTrackingService {

    fun processTransaction(tx: TransactionDto)

}

interface StateService {

    fun getByWalletId(walletId: Long): State

    fun save(state: State)

}

interface WalletService {

    fun create(url: String, integrations: Set<CreateIntegrationRequest>)

    fun getByBlockchainAddress(blockchainId: Long, address: String): Wallet?

}

interface TransactionService {

    fun save(transaction: Transaction): Transaction

    fun get(id: Long, walletId: Long): Transaction

    fun getAllByWalletId(walletId: Long, pageable: Pageable): Page<Transaction>

}

interface WebHookService {

    fun save(webHook: WebHook): WebHook

}

interface BlockchainService {

    fun get(id: Long): Blockchain

    fun getAll(): List<Blockchain>

}
