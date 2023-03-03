package io.openfuture.state.repository

import io.openfuture.state.service.dto.Watch
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface WatchRepository: ReactiveMongoRepository<Watch, String> {
    suspend fun existsByWatchId(watchId: String): Mono<Boolean>
}