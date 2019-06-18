package io.openfuture.state.entity

import javax.persistence.*

@Entity
@Table(name = "integrations")
class Integration(

        @OneToMany(mappedBy = "integration")
        var transactions: List<Transaction> = emptyList(),

        @OneToMany(mappedBy = "integration")
        var events: List<Event> = emptyList()

) : BaseModel()