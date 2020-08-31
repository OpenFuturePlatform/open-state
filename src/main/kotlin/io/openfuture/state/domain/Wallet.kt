package io.openfuture.state.domain

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.LocalDateTime

@Document
data class Wallet(
        @Indexed(unique = true) val address: String,
        val webhook: String,
        val transactions: MutableSet<Transaction> = mutableSetOf(),
        var lastUpdate: LocalDateTime = LocalDateTime.now(),
        @MongoId val id: ObjectId = ObjectId(),
)
