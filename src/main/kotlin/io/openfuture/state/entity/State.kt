package io.openfuture.state.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "states")
class State(

        @Column(name = "root", nullable = false)
        var root: String,

        @OneToMany(mappedBy = "state")
        var integrations: Set<Integration>

) : BaseModel()