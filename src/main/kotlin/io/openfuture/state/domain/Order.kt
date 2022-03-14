package io.openfuture.state.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.math.BigDecimal
import java.time.LocalDateTime

@Document
data class Order(
    @MongoId
    val id: String = ObjectId().toHexString(),
    @LastModifiedDate
    var lastUpdate: LocalDateTime = LocalDateTime.now(),
    var placedAt: LocalDateTime = LocalDateTime.now(),
    @Indexed
    val orderId: String,
    @Indexed
    val orderKey: String,
    val amount: BigDecimal,//in USD
    val productCurrency: String,//USD
    var paid: BigDecimal = BigDecimal.ZERO,//USD
    val source: String,
    val webhook: String
) {
    constructor(
        orderId: String,
        orderKey: String,
        amount: BigDecimal,
        productCurrency: String,
        source: String,
        webhook: String
    ) : this(
        ObjectId().toHexString(), LocalDateTime.now(), LocalDateTime.now(), orderId, orderKey, amount, productCurrency, BigDecimal.ZERO, source, webhook
    )
}