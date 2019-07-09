package io.openfuture.state.domain.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class UpdateAccountWebHookRequest(
        @field:NotNull val id: Long,
        @field:NotBlank val webHook: String
)
