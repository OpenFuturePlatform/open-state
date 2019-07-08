package io.openfuture.state.domain.request

import javax.validation.constraints.NotBlank

data class CreateIntegrationRequest(
        @field:NotBlank val address: String,
        val blockchainId: Long
)
