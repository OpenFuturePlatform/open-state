package io.openfuture.state.entity

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

        @OneToOne
        @JoinColumn(name = "integration_id")
        var integration: Integration,

        @Column(name = "disabled", nullable = false)
        var disabled: Boolean = false

) : BaseModel()