package io.openfuture.state.entity

import io.openfuture.state.util.DictionaryUtils
import io.openfuture.state.util.HashUtils
import org.apache.commons.codec.binary.Hex
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.nio.ByteBuffer

@Document
class Transaction(
        @MongoId
        val id: ObjectId = ObjectId(),
        var walletAddress: String,
        var hash: String,
        var externalHash: String,
        var typeId: Int,
        var participant: String,
        var amount: Long,
        var fee: Long,
        var date: Long,
        var blockHeight: Long,
        var blockHash: String
) {

    fun getType(): TransactionType = DictionaryUtils.valueOf(TransactionType::class.java, typeId)

    companion object {
        fun generateHash(address: String, typeId: Int, participantAddress: String, amount: Long, fee: Long, date: Long): String {
            val bytes = ByteBuffer.allocate(address.toByteArray().size + Int.SIZE_BYTES +
                    participantAddress.toByteArray().size + Long.SIZE_BYTES + Long.SIZE_BYTES + Long.SIZE_BYTES)
                    .put(address.toByteArray())
                    .putInt(typeId)
                    .put(participantAddress.toByteArray())
                    .putLong(amount)
                    .putLong(fee)
                    .putLong(date)
                    .array()

            return Hex.encodeHexString(HashUtils.sha256(bytes))
        }

    }

}
