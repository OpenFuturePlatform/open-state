package io.openfuture.state.openchain.component.openrpc.dto

class BasePayloadDto<T>(
        val totalCount: Long,
        val list: List<T>
)