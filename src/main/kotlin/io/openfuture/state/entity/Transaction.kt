package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
import io.openfuture.state.util.DictionaryUtils
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "transactions")
class Transaction(

        @ManyToOne
        @JoinColumn(name = "wallet_id")
        var wallet: Wallet,

        @Column(name = "hash", nullable = false)
        var hash: String,

        @Column(name = "type_id", nullable = false)
        var type_id: Int,

        @Column(name = "participant", nullable = false)
        var participant: String,

        @Column(name = "amount", nullable = false)
        var amount: Long,

        @Column(name = "date", nullable = false)
        var date: LocalDateTime,

        @Column(name = "block_height", nullable = false)
        var blockHeight: Long,

        @Column(name = "block_hash", nullable = false)
        var blockHash: String

) : BaseModel() {

    fun getType(): TransactionType = DictionaryUtils.valueOf(TransactionType::class.java, type_id)

}
