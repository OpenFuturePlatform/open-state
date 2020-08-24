package io.openfuture.state.repository.transaction

import io.openfuture.state.entity.Transaction
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface TransactionRepository : ReactiveMongoRepository<Transaction, String> {

    fun findByIdAndWalletAddress(id: String, walletAddress: String): Mono<Transaction>

    fun findAllByWalletAddressOrderByDateDesc(walletAddress: String, pageable: Pageable): Flux<Transaction>

    fun countByWalletAddress(walletAddress: String): Mono<Long>

}