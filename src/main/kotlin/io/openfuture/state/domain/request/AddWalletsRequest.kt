package io.openfuture.state.domain.request

import javax.validation.constraints.NotEmpty

data class AddWalletsRequest(
        @field:NotEmpty val integrations: Set<CreateIntegrationRequest> = setOf()
)
