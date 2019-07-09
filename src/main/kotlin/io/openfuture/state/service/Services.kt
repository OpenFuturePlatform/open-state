package io.openfuture.state.service

import io.openfuture.state.domain.dto.TransactionDto
import io.openfuture.state.domain.request.CreateIntegrationRequest
import io.openfuture.state.entity.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable


interface StateTrackingService {

    fun processTransaction(tx: TransactionDto)

}

interface AccountService {

    fun save(account: Account, integrations: Set<CreateIntegrationRequest>): Account

    fun get(id: Long): Account

    fun update(id: Long, webHook: String): Account

    fun addWallets(id: Long, integrations: Set<CreateIntegrationRequest>): Account

}

interface StateService {

    fun save(state: State): State

    fun get(id: Long): State

}

interface WalletService {

    fun save(wallet: Wallet): Wallet

    fun getByBlockchainAddress(blockchainId: Long, address: String): Wallet?

    fun getAllByAccount(accountId: Long): List<Wallet>

    fun get(id: Long, accountId: Long): Wallet

}

interface TransactionService {

    fun save(transaction: Transaction): Transaction

    fun get(id: Long, walletId: Long): Transaction

    fun getAllByWalletId(walletId: Long, pageable: Pageable): Page<Transaction>

}

interface BlockchainService {

    fun get(id: Long): Blockchain

    fun getAll(): List<Blockchain>

}
