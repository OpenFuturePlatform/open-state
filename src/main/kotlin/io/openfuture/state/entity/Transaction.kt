package io.openfuture.state.entity

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "transactions")
class Transaction(

        @Column(name = "hash", nullable = false)
        var hash: String,

        @Column(name = "data", nullable = false)
        var data: String,

        @Column(name = "date", nullable = false)
        var date: LocalDateTime,

        @ManyToOne
        @JoinColumn(name = "integration_id", nullable = false)
        var integration: Integration

) : BaseModel()
