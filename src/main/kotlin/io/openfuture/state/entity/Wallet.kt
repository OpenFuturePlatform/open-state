package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "wallets")
class Wallet(

        @ManyToOne
        @JoinColumn(name = "web_hook_id", nullable = false)
        var webHook: WebHook,

        @ManyToOne
        @JoinColumn(name = "blockchain_id")
        var blockchain: Blockchain,

        @Column(name = "address", nullable = false)
        var address: String,

        @Column(name = "start_tracking_date", nullable = false)
        var startTrackingDate: LocalDateTime = LocalDateTime.now(),

        @Column(name = "is_active", nullable = false)
        var isActive: Boolean = true

) : BaseModel()
