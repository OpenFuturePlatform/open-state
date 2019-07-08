package io.openfuture.state.domain.request

import javax.validation.constraints.NotBlank

data class UpdateAccountWebHookRequest(
        val id: Long,
        @field:NotBlank val webHook: String
)
