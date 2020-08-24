package io.openfuture.state.repository

import io.openfuture.state.entity.OpenScaffold
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface ScaffoldRepository : ReactiveMongoRepository<OpenScaffold, String> {

    fun findByRecipientAddress(recipientAddress: String): Mono<OpenScaffold>

}