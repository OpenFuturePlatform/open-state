package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "coins")
class Coin(

        @Column(name = "title", nullable = false)
        var title: String,

        @Column(name = "short_title", nullable = false)
        var shortTitle: String,

        @Column(name = "decimals", nullable = false)
        var decimals: Int

) : BaseModel()
