package io.openfuture.state.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.LocalDateTime

@Document
data class Wallet(
        @MongoId
        val id: ObjectId = ObjectId(),
        @Indexed(unique = true)
        val address: String,
        val webhook: String,
        val transactions: Set<Transaction>,
        var lastUpdateDate: LocalDateTime
)
