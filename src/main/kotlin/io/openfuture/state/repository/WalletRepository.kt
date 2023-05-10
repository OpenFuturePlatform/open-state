package io.openfuture.state.repository

import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WalletIdentity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface WalletRepository : ReactiveMongoRepository<Wallet, String> {

    fun findByIdentity(identity: WalletIdentity): Mono<Wallet>

    fun existsByIdentity(identity: WalletIdentity): Mono<Boolean>

    fun findFirstByIdentityAddress(address: String): Mono<Wallet>

    fun findAllByUserData_Order_OrderKey(orderKey: String): Flux<Wallet>

    fun findFirstByUserData_Order_OrderKey(orderKey: String): Mono<Wallet>

    fun deleteByIdentity(identity: WalletIdentity)

    fun findAllByApplicationId(applicationId: String): Flux<Wallet>

}
