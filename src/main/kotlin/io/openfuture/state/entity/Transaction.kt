package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
import io.openfuture.state.util.DictionaryUtils
import io.openfuture.state.util.HashUtils
import org.apache.tomcat.util.buf.HexUtils
import java.nio.ByteBuffer
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "transactions")
class Transaction(

        @ManyToOne
        @JoinColumn(name = "wallet_id")
        var wallet: Wallet,

        @Column(name = "hash", nullable = false)
        var hash: String,

        @Column(name = "external_hash", nullable = false)
        var externalHash: String,

        @Column(name = "type_id", nullable = false)
        var typeId: Int,

        @Column(name = "participant", nullable = false)
        var participant: String,

        @Column(name = "amount", nullable = false)
        var amount: Long,

        @Column(name = "date", nullable = false)
        var date: Long,

        @Column(name = "block_height", nullable = false)
        var blockHeight: Long,

        @Column(name = "block_hash", nullable = false)
        var blockHash: String

) : BaseModel() {

    fun getType(): TransactionType = DictionaryUtils.valueOf(TransactionType::class.java, typeId)

    companion object {
        fun generateHash(address: String, typeId: Int, participantAddress: String, amount: Long, date: Long): String {
            val bytes = ByteBuffer.allocate(address.toByteArray().size + Int.SIZE_BYTES +
                    participantAddress.toByteArray().size + Long.SIZE_BYTES + Long.SIZE_BYTES)
                    .put(address.toByteArray())
                    .putInt(typeId)
                    .put(participantAddress.toByteArray())
                    .putLong(amount)
                    .putLong(date)
                    .array()

            return HexUtils.toHexString(HashUtils.sha256(bytes))
        }

    }

}
