package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
import javax.persistence.*

@Entity
@Table(name = "blockchains")
class Blockchain(

        @Column(name = "blockchain_type_id", nullable = false)
        var blockchainTypeId: Int,

        @Column(name = "network_url", nullable = false)
        var networkUrl: String,

        @Column(name = "private_key", nullable = false)
        var privateKey: String,

        @Column(name = "currency", nullable = false)
        var currency: String,

        @Column(name = "decimals")
        var decimals: Int

) : BaseModel()
