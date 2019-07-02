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

    fun findByIdAndWalletId(id: Long, walletId: Long): Transaction

    fun findAllByWalletIdOrderByDateDesc(walletId: Long, pageable: Pageable): Page<Transaction>

}

@Repository
interface WalletRepository : BaseRepository<Wallet> {

    fun findByBlockchainIdAndAddress(blockchainId: Long, address: String): Wallet?

    fun findAllByAccountId(accountId: Long): List<Wallet>

    fun findByIdAndAccountId(id: Long, accountId: Long): Wallet

}

@Repository
interface AccountRepository : BaseRepository<Account>
