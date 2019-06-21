package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "states_history")
class StateHistory(

        @Column(name = "address", nullable = false)
        var address: String,

        @Column(name = "balance", nullable = false)
        var balance: Long = 0,

        @Column(name = "date", nullable = false)
        var date: LocalDateTime = LocalDateTime.now(),

        @ManyToOne
        @JoinColumn(name = "state_id")
        var state: State

) : BaseModel()
