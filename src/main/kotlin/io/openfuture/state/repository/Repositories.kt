package io.openfuture.state.repository

import io.openfuture.state.entity.*
import io.openfuture.state.entity.base.BaseModel
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
interface StateRepository : BaseRepository<State> {

    fun findByWallet(wallet: Wallet): State

}

@Repository
interface TransactionRepository : BaseRepository<Transaction> {


}

@Repository
interface WalletRepository : BaseRepository<Wallet> {

    fun findByBlockchainIdAndAddress(blockchainId: Long, address: String): Wallet

}

@Repository
interface WebHookRepository : BaseRepository<WebHook>
