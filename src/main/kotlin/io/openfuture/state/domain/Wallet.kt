package io.openfuture.state.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
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
    @CreatedDate
    var createdDate: LocalDateTime =  LocalDateTime.now(),
    @MongoId
    val id: String = ObjectId().toHexString(),
    var userData: UserData,
    var walletType: WalletType
)

data class UserData(
    val id: String = ObjectId().toHexString(),
    var nonce: Int = 0,
    var order: Order? = null,
    var userId: String? = null,
    val rate: BigDecimal = BigDecimal.ZERO,
    val metadata: Any? = null
)