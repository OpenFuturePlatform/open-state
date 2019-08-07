package io.openfuture.state.controller.domain.request

import javax.validation.Valid
import javax.validation.constraints.NotEmpty

data class AddWalletsRequest(
        @field:NotEmpty var integrations: Set<@Valid CreateIntegrationRequest> = setOf()
)
