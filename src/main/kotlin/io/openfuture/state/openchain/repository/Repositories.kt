package io.openfuture.state.openchain.repository

import io.openfuture.state.openchain.entity.OpenTrackingLog
import io.openfuture.state.openchain.entity.OpenTransferTransaction
import io.openfuture.state.repository.BaseRepository
import org.springframework.stereotype.Repository

@Repository
interface OpenTrackingLogRepository : BaseRepository<OpenTrackingLog> {

    fun findFirstByOrderByIdDesc(): OpenTrackingLog?

}

@Repository
interface OpenTransferTransactionRepository : BaseRepository<OpenTransferTransaction>