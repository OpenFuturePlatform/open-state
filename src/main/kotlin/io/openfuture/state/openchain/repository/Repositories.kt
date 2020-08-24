package io.openfuture.state.openchain.repository

import io.openfuture.state.openchain.entity.OpenTrackingLog
import io.openfuture.state.openchain.entity.OpenTransferTransaction
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface OpenTrackingLogRepository : ReactiveMongoRepository<OpenTrackingLog, String> {

    fun findFirstByOrderByIdDesc(): OpenTrackingLog?

}

@Repository
interface OpenTransferTransactionRepository : ReactiveMongoRepository<OpenTransferTransaction, String>