package io.openfuture.state.controller.request

class WalletInfoRequest(
    var applicationId: String,
    var username: String,
    var email: String,
    var metadata: Map<String, String>
)