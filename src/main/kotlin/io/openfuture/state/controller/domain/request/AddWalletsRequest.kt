package io.openfuture.state.controller.domain.request

import javax.validation.constraints.NotEmpty

data class AddWalletsRequest(
        @field:NotEmpty val integrations: Set<CreateIntegrationRequest> = setOf()
)
