package io.openfuture.state.openchain.service

import io.openfuture.state.openchain.entity.OpenTrackingLog
import io.openfuture.state.openchain.entity.OpenTransferTransaction

interface OpenTransactionTrackingService {

    fun processTransferTransaction()

}

interface OpenTrackingLogService {

    fun getLastOpenTrackingLog(): OpenTrackingLog?

    fun save(offset: Long, hash: String): OpenTrackingLog

}

interface OpenTransferTransactionService {

    fun findByHashes(hashes: List<String>): List<OpenTransferTransaction>?

}