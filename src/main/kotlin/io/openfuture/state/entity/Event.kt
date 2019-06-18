package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "events")
class Event(

        @Column(name = "name", nullable = false)
        var name: String,

        @Column(name = "data", nullable = false)
        var data: String,

        @Column(name = "date", nullable = false)
        var date: LocalDateTime,

        @ManyToOne
        @JoinColumn(name = "integration_id", nullable = false)
        var integration: Integration

) : BaseModel()
