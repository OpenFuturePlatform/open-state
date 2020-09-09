package io.openfuture.state.repository

import io.openfuture.state.domain.Wallet
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface WalletRepository : ReactiveMongoRepository<Wallet, String> {

    fun findByAddress(address: String): Mono<Wallet>

    fun existsByBlockchainAndAddress(blockchain: String, address: String): Mono<Boolean>

    fun findByBlockchainAndAddress(blockchain: String, address: String): Mono<Wallet>
}
