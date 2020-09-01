package io.openfuture.state.repository

import io.openfuture.state.domain.Wallet
import io.openfuture.state.model.Blockchain
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface WalletRepository : ReactiveMongoRepository<Wallet, String> {

    fun findByAddress(address: String): Mono<Wallet>

    fun existsByAddressAndBlockchain(address: String, blockchain: Blockchain): Mono<Boolean>
}
