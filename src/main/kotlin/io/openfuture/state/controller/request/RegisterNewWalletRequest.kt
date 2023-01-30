package io.openfuture.state.controller.request

class RegisterNewWalletRequest(
    var webhook: String,
    val blockchains: ArrayList<BlockChainDataRequest>,
    var walletInfo: WalletInfoRequest
)