package io.openfuture.state.controller.request

data class BalanceRequest(
    val blockchainName: String,
    val isNative: Boolean,
    val address: String)
