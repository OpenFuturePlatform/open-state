package io.openfuture.state.controller.request

data class BlockChainDataRequest(
    val address: String,
    val blockchain: String
)