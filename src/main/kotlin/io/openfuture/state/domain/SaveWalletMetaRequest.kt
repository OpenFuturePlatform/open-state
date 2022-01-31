package io.openfuture.state.domain

import javax.validation.constraints.NotBlank

data class SaveWalletMetaRequest(
    @field:NotBlank
    val address: String?,
    @field:NotBlank
    val webhook: String?,
    @field:NotBlank
    val blockchain: String?,
    val metadata: WalletMetaData
)
