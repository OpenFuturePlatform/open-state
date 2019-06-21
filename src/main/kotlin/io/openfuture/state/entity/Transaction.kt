package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
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

        @Column(name = "type", nullable = false)
        var type: String,

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

) : BaseModel()
