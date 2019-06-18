package io.openfuture.state.entity

import javax.persistence.*

@Entity
@Table(name = "integrations")
class Integration(

        @Column(name = "address", nullable = false)
        var address: String,

        @Column(name = "balance", nullable = false)
        var balance: Long = 0,

        @ManyToOne
        @JoinColumn(name = "state_id")
        var state: State,

        @Column(name = "disabled", nullable = false)
        var disabled: Boolean = false

) : BaseModel()