package io.openfuture.state.domain.request

data class UpdateAccountWebHookRequest(
        val id: Long,
        val webHook: String
)
