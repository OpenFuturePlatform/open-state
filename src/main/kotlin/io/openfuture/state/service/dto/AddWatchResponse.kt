package io.openfuture.state.service.dto

data class AddWatchResponse(
    val id: String,
    val webhook: String,
    val metadata: Any,
    val wallets: List<WatchWalletResponse>
)

data class WatchWalletResponse(
    val blockchain: String,
    val address: String,
)
