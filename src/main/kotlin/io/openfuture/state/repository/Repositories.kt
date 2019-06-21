package io.openfuture.state.repository

import io.openfuture.state.entity.*
import io.openfuture.state.entity.base.BaseModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.stereotype.Repository

@NoRepositoryBean
interface BaseRepository<T : BaseModel> : JpaRepository<T, Long>

@Repository
interface AccountRepository : BaseRepository<Account>

@Repository
interface StateRepository : BaseRepository<State>

@Repository
interface BlockchainRepository : BaseRepository<Blockchain>

@Repository
interface TransactionRepository : BaseRepository<Transaction>

@Repository
interface StateHistoryRepository : BaseRepository<StateHistory>
