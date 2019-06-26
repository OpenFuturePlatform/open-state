package io.openfuture.state.domain.request

data class CreateWalletRequest(
        val webHook: String,
        val integrations: Set<CreateIntegrationRequest>
)
