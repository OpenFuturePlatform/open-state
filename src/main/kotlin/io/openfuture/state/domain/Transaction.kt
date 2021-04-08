package io.openfuture.state.domain

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.math.BigDecimal
import java.time.LocalDateTime

@Document
data class Transaction(
        @Indexed
        val walletIdentity: WalletIdentity,
        val hash: String,
        val from: Set<String>,
        val to: String,
        val amount: BigDecimal,
        val date: LocalDateTime,
        val blockHeight: Long,
        val blockHash: String,
        @MongoId
        val id: String = ObjectId().toHexString()
)
