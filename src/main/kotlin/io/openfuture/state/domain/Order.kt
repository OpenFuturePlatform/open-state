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
    val orderKey: String,
    val applicationId: String,
    val amount: BigDecimal,//in USD
    val productCurrency: String,//USD
    var paid: BigDecimal = BigDecimal.ZERO//USD
) {
    constructor(
        orderKey: String,
        applicationId: String,
        amount: BigDecimal,
        productCurrency: String
    ) : this(
        ObjectId().toHexString(), LocalDateTime.now(), LocalDateTime.now(), orderKey, applicationId, amount, productCurrency, BigDecimal.ZERO
    )
}