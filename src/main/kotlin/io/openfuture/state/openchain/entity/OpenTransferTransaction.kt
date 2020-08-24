package io.openfuture.state.openchain.entity

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId

@Document
class OpenTransferTransaction(
        @MongoId
        val id: ObjectId = ObjectId(),
        var fee: Long,
        var amount: Long,
        var hash: String,
        var senderAddress: String,
        var recipientAddress: String?,
        var blockHash: String,
        var date: Long,
        var webHook: String?
)