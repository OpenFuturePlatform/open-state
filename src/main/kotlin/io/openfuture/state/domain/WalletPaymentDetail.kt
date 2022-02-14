package io.openfuture.state.domain

data class WalletPaymentDetail(
    var orderId: String,
    var orderKey: String,
    var amount: String,
    var productCurrency: String,
    var source: String,
    val paymentCurrency: String
)
