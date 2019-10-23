package io.openfuture.state.openchain.entity

import io.openfuture.state.entity.base.BaseModel
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "open_tracking_logs")
class OpenTrackingLog(

        @Column(name = "`offset`", nullable = false)
        var offset: Long,

        @Column(name = "hash", nullable = false)
        var hash: String

) : BaseModel()