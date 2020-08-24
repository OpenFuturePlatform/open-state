package io.openfuture.state.openchain.service

import io.openfuture.state.openchain.entity.OpenTrackingLog
import io.openfuture.state.openchain.entity.OpenTransferTransaction

interface OpenTransactionTrackingService {

    suspend fun processTransferTransaction()

}

interface OpenTrackingLogService {

    suspend fun getLastOpenTrackingLog(): OpenTrackingLog?

    suspend fun save(offset: Long, hash: String): OpenTrackingLog

}

interface OpenTransferTransactionService {

    suspend fun save(openTransferTransaction: OpenTransferTransaction): OpenTransferTransaction

}