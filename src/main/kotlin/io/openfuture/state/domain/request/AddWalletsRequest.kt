package io.openfuture.state.domain.request

data class AddWalletsRequest(
        val integrations: Set<CreateIntegrationRequest> = setOf()
)
