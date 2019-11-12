package io.openfuture.state.repository

import io.openfuture.state.entity.*
import io.openfuture.state.entity.base.BaseModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.stereotype.Repository

@NoRepositoryBean
interface BaseRepository<T : BaseModel> : JpaRepository<T, Long>

@Repository
interface CoinRepository : BaseRepository<Coin>

@Repository
interface BlockchainRepository : BaseRepository<Blockchain>

@Repository
interface StateRepository : BaseRepository<State>

@Repository
interface TransactionRepository : BaseRepository<Transaction> {

    fun findByIdAndWalletId(id: Long, walletId: Long): Transaction?

    fun findAllByWalletIdOrderByDateDesc(walletId: Long, pageable: Pageable): Page<Transaction>

}

@Repository
interface WalletRepository : BaseRepository<Wallet> {

    fun findByBlockchainIdAndAddressIgnoreCase(blockchainId: Long, address: String): Wallet?

    fun findByBlockchainIdAndAddressIgnoreCaseAndIsActiveTrue(blockchainId: Long, address: String): Wallet?

    fun findAllByAccountsContainsAndIsActiveTrue(account: Account): List<Wallet>

    fun findByIdAndAccountsContainsAndIsActiveTrue(id: Long, account: Account): Wallet?

}

@Repository
interface AccountRepository : BaseRepository<Account> {

    fun findByIdAndIsEnabledTrue(id: Long): Account?

}

@Repository
interface ScaffoldRepository : BaseRepository<OpenScaffold> {

    fun findByRecipientAddress(recipientAddress: String): OpenScaffold?

}