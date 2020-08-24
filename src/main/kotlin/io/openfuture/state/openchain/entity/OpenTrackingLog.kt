package io.openfuture.state.openchain.entity

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId

@Document
class OpenTrackingLog(
        @MongoId
        val id: ObjectId = ObjectId(),
        var offset: Long,
        var hash: String
)