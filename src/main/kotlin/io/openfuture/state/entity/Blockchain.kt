package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
import javax.persistence.*

@Entity
@Table(name = "integrations")
class Blockchain(

        @Column(name = "blockchain_type_id", nullable = false)
        var blockchainTypeId: Int,

        @Column(name = "network_url", nullable = false)
        var networkUrl: String,

        @Column(name = "private_key", nullable = false)
        var privateKey: String

) : BaseModel()
