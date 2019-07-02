package io.openfuture.state.domain.request

data class CreateAccountRequest(
        val webHook: String,
        val integrations: Set<CreateIntegrationRequest>
)
