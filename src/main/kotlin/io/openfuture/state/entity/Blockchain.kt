package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
import javax.persistence.*

@Entity
@Table(name = "blockchains")
class Blockchain(

        @OneToOne
        @JoinColumn(name = "coin_id")
        var coin: Coin,

        @Column(name = "title", nullable = false)
        var title: String

) : BaseModel()
