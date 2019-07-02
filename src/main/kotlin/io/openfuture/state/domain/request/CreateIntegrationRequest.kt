package io.openfuture.state.domain.request

data class CreateIntegrationRequest(
        val address: String,
        val blockchainId: Long
)
