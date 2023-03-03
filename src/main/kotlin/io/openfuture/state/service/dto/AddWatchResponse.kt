package io.openfuture.state.service.dto

data class AddWatchResponse(
    val webhook: String,
    val watch: Watch,
    val wallets: List<WatchWalletResponse>
)

data class WatchWalletResponse(
    val blockchain: String,
    val address: String,
)
