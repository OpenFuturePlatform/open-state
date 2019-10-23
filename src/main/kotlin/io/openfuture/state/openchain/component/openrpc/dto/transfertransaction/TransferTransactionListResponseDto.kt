package io.openfuture.state.openchain.component.openrpc.dto.transfertransaction

import io.openfuture.state.openchain.component.openrpc.dto.BasePayloadDto
import io.openfuture.state.openchain.component.openrpc.dto.BaseResponseDto

class TransferTransactionListResponseDto(
        timestamp: Long,
        version: String,
        val payload: BasePayloadDto<TransferTransactionDto>
) : BaseResponseDto(timestamp, version)