package io.openfuture.state.openchain.component.openrpc.dto.balance

import io.openfuture.state.openchain.component.openrpc.dto.BaseResponseDto

class BalanceResponseDto(
        timestamp: Long,
        version: String,
        val payload: Long
) : BaseResponseDto(timestamp, version)