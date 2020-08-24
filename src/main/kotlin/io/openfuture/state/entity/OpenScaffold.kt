package io.openfuture.state.entity

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class OpenScaffold(
        val id: ObjectId = ObjectId(),
        var recipientAddress: String,
        var webHook: String
)