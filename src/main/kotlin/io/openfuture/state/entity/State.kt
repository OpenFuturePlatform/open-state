package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "states")
class State(

        @OneToOne
        @JoinColumn(name = "wallet_id")
        var wallet: Wallet,

        @Column(name = "balance", nullable = false)
        var balance: Long = 0,

        @Column(name = "root", nullable = false)
        var root: String,

        @Column(name = "date", nullable = false)
        var date: LocalDateTime = LocalDateTime.now()

) : BaseModel()
