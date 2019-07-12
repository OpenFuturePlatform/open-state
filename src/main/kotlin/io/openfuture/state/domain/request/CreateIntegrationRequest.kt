package io.openfuture.state.domain.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class CreateIntegrationRequest(
        @field:NotBlank val address: String,
        @field:NotNull val blockchainId: Long
)
