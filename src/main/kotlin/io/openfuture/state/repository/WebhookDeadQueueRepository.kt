package io.openfuture.state.repository

import io.openfuture.state.domain.WalletAddress
import io.openfuture.state.domain.WebhookDeadQueue
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface WebhookDeadQueueRepository : ReactiveMongoRepository<WebhookDeadQueue, String> {

    fun findByWalletAddress(address: WalletAddress): Mono<WebhookDeadQueue>

    fun existsByWalletAddress(address: WalletAddress): Mono<Boolean>

    fun deleteByWalletAddress(address: WalletAddress): Mono<Void>
}
