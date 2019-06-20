package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "states")
class State(

        @Column(name = "address", nullable = false)
        var address: String,

        @Column(name = "balance", nullable = false)
        var balance: Long = 0,

        @Column(name = "root", nullable = false)
        var root: String,

        @Column(name = "last_updated", nullable = false)
        var lastUpdated: LocalDateTime = LocalDateTime.now(),

        @Column(name = "path_phrase", nullable = true)
        var seedPhrase: String? = null,

        @OneToOne
        @JoinColumn(name = "blockchain_id")
        var blockchain: Blockchain,

        @Column(name = "disabled", nullable = false)
        var disabled: Boolean = false

) : BaseModel()
