package io.openfuture.state.service.dto

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId

@Document
data class Watch(
    @Indexed
    var watchId: String,
    var applicationId: String,
    var metadata: Any
){
    @MongoId
    var id: String = ObjectId().toHexString()
}