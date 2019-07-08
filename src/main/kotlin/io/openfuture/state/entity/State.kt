package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
import io.openfuture.state.util.HashUtils
import org.apache.tomcat.util.buf.HexUtils
import java.nio.ByteBuffer
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "states")
class State(

        @Column(name = "balance", nullable = false)
        var balance: Double = 0.0,

        @Column(name = "root", nullable = false)
        var root: String,

        @Column(name = "date", nullable = false)
        var date: Long = Date().time

) : BaseModel() {

    companion object {
        fun generateHash(walletAddress: String, balance: Double = 0.0, date: Long = Date().time): String {
            val bytes = ByteBuffer.allocate(walletAddress.toByteArray().size + Long.SIZE_BYTES +
                    Long.SIZE_BYTES)
                    .put(walletAddress.toByteArray())
                    .putDouble(balance)
                    .putLong(date)
                    .array()

            return HexUtils.toHexString(HashUtils.sha256(bytes))
        }

    }

}
