package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "wallets")
class Wallet(

        @ManyToOne
        @JoinColumn(name = "account_id", nullable = false)
        var account: Account,

        @ManyToOne
        @JoinColumn(name = "blockchain_id")
        var blockchain: Blockchain,

        @Column(name = "address", nullable = false)
        var address: String,

        @OneToOne
        @JoinColumn(name = "state_id")
        var state: State,

        @Column(name = "start_tracking_date", nullable = false)
        var startTrackingDate: Long = Date().time,

        @Column(name = "is_active", nullable = false)
        var isActive: Boolean = true

) : BaseModel()
