package io.openfuture.state.service

import io.openfuture.state.controller.domain.dto.TransactionDto
import io.openfuture.state.controller.domain.request.SaveOpenScaffoldRequest
import io.openfuture.state.entity.OpenScaffold


interface StateTrackingService {

    suspend fun processTransaction(tx: TransactionDto)

    suspend fun isTrackedAddress(address: String, blockchainId: Long): Boolean

}

interface OpenScaffoldService {

    suspend fun save(request: SaveOpenScaffoldRequest): OpenScaffold

    suspend fun findByRecipientAddress(addresses: String): OpenScaffold?
}