package io.openfuture.state.repository

import io.openfuture.state.domain.WebhookExecution
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface WebhookExecutionRepository : ReactiveMongoRepository<WebhookExecution, String> {

    fun findByTransactionId(transactionId: String): Mono<WebhookExecution>
}
