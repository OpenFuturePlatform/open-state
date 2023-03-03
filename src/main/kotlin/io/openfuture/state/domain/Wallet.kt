package io.openfuture.state.domain

import io.openfuture.state.service.dto.Watch
import org.bson.types.ObjectId
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.MongoId
import java.math.BigDecimal
import java.time.LocalDateTime

@Document
data class Wallet(
    @Indexed
    val identity: WalletIdentity,
    val webhook: String,
    val applicationId: String,
    @LastModifiedDate
    var lastUpdate: LocalDateTime = LocalDateTime.now(),
    @MongoId
    val id: String = ObjectId().toHexString(),
    val rate: BigDecimal = BigDecimal.ZERO,
    var nonce: Int = 0,
    @Field("order")
    var order: Order? = null,
    var watch: Watch? = null
)
