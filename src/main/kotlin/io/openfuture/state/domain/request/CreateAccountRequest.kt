package io.openfuture.state.domain.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

data class CreateAccountRequest(
        @field:NotBlank val webHook: String,
        @field:NotEmpty val integrations: Set<CreateIntegrationRequest>
)
