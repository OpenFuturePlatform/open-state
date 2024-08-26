package io.openfuture.state.controller.request

data class BalanceRequest(
    val blockchainName: String,
    val contractAddress: String?,
    val address: String)
