package io.openfuture.state.domain.request

import javax.validation.constraints.NotBlank

data class CreateAccountRequest(
        @field:NotBlank val webHook: String,
        val integrations: Set<CreateIntegrationRequest>
)
