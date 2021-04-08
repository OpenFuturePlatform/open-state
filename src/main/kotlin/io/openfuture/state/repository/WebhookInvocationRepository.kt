package io.openfuture.state.repository

import io.openfuture.state.domain.WebhookInvocation
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface WebhookInvocationRepository : ReactiveMongoRepository<WebhookInvocation, String> {

    fun findByTransactionId(transactionId: String): Mono<WebhookInvocation>
}
