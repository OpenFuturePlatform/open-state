package io.openfuture.state.controller.domain.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class DeleteWalletRequest(
        @field:NotBlank var address: String,
        @field:NotNull var blockchainId: Long
)
