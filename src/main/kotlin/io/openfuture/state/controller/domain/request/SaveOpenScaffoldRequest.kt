package io.openfuture.state.controller.domain.request

import javax.validation.constraints.NotBlank

data class SaveOpenScaffoldRequest(
        @field:NotBlank val address: String,
        @field:NotBlank val webHook: String
)
