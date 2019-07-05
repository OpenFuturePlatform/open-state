package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
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

) : BaseModel()
