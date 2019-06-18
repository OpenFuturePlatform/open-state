package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
import javax.persistence.*

@Entity
@Table(name = "integrations")
class Integration(

        @Column(name = "blockchain_id", nullable = false)
        var blockchainId: Int,

        @OneToMany(mappedBy = "integration")
        var transactions: List<Transaction> = emptyList(),

        @OneToMany(mappedBy = "integration")
        var events: List<Event> = emptyList()

) : BaseModel()
