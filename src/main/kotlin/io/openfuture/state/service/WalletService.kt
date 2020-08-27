package io.openfuture.state.service

import io.openfuture.state.controller.domain.dto.WalletDto
import io.openfuture.state.controller.domain.request.SaveWalletRequest

interface WalletService {

    suspend fun save(request: SaveWalletRequest): WalletDto

    suspend fun findByAddress(address: String): WalletDto
}
