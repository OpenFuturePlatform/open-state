package io.openfuture.state.repository

import io.openfuture.state.model.Wallet
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface WalletRepository : ReactiveMongoRepository<Wallet, String> {

    fun findByAddress(address: String): Mono<Wallet>
}
