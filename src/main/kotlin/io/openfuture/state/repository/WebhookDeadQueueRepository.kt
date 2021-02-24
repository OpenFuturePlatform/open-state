package io.openfuture.state.repository

import io.openfuture.state.domain.WebhookDeadQueue
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface WebhookDeadQueueRepository : ReactiveMongoRepository<WebhookDeadQueue, String> {

    fun findByWalletAddress(walletAddress: String): Mono<WebhookDeadQueue>

    fun existsByWalletAddress(walletAddress: String): Mono<Boolean>

    fun deleteByWalletAddress(walletAddress: String): Mono<Void>
}
