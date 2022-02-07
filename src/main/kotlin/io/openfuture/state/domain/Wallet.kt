package io.openfuture.state.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank

@Document
data class Wallet(
    @Indexed
    val identity: WalletIdentity,
    var webhook: String,
    var webhookStatus: WebhookStatus = WebhookStatus.NOT_INVOKED,
    @LastModifiedDate
    var lastUpdate: LocalDateTime = LocalDateTime.now(),
    @MongoId
    val id: String = ObjectId().toHexString(),
    var orderId: String,
    var orderKey: String,
    var amount: BigDecimal,
    var productCurrency: String,
    var source: String,
    val paymentCurrency: String,
    var totalPaid: BigDecimal = BigDecimal.ZERO,
    var rate: BigDecimal
)
