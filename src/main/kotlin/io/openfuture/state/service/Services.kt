package io.openfuture.state.service

import io.openfuture.state.controller.domain.dto.TransactionDto
import io.openfuture.state.controller.domain.request.CreateIntegrationRequest
import io.openfuture.state.entity.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable


interface StateTrackingService {

    fun processTransaction(tx: TransactionDto)

    fun isTrackedAddress(address: String, blockchainId: Long): Boolean

}

interface AccountService {

    fun save(account: Account, integrations: Set<CreateIntegrationRequest>): Account

    fun get(id: Long): Account

    fun update(id: Long, webHook: String): Account

    fun addWallets(id: Long, integrations: Set<CreateIntegrationRequest>): Account

    fun deleteWallet(accountId: Long, walletId: Long): Account

    fun deleteWalletByAddress(accountId: Long, address: String, blockchainId: Long): Account

    fun delete(id: Long): Account

}

interface StateService {

    fun save(state: State): State

    fun get(id: Long): State

}

interface WalletService {

    fun save(wallet: Wallet): Wallet

    fun getByBlockchainAddress(blockchainId: Long, address: String): Wallet?

    fun getActiveByBlockchainAddress(blockchainId: Long, address: String): Wallet?

    fun getAllByAccount(account: Account): List<Wallet>

    fun get(id: Long, account: Account): Wallet

    fun deleteByAccount(account: Account, wallet: Wallet)

    fun deleteAllByAccount(account: Account)

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

interface IntegrationService {

    fun getBalance(address: String, blockchain: Blockchain): Long

}
