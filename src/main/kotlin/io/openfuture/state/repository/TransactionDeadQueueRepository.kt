package io.openfuture.state.repository

import io.openfuture.state.domain.TransactionDeadQueue
import io.openfuture.state.domain.WalletIdentity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface TransactionDeadQueueRepository : ReactiveMongoRepository<TransactionDeadQueue, String> {

    suspend fun findByWalletIdentity(walletIdentity: WalletIdentity): Mono<TransactionDeadQueue>

    suspend fun existsByWalletIdentity(walletIdentity: WalletIdentity): Mono<Boolean>

    suspend fun deleteByWalletIdentity(walletIdentity: WalletIdentity)

}
