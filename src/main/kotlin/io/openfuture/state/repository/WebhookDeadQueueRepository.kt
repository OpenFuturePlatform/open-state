package io.openfuture.state.repository

import io.openfuture.state.domain.WebhookDeadQueue
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface WebhookDeadQueueRepository : ReactiveMongoRepository<WebhookDeadQueue, String> {

    fun findByWalletKey(walletKey: String): Mono<WebhookDeadQueue>

    fun existsByWalletKey(walletKey: String): Mono<Boolean>

    fun deleteByWalletKey(walletKey: String): Mono<Void>
}
