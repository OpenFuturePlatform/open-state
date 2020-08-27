package io.openfuture.state.controller.domain.request

data class SaveWalletRequest(
        val address: String,
        val webhook: String,
)
