package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "transactions")
class Transaction(

        @Column(name = "hash", nullable = false)
        var hash: String,

        @Column(name = "from_address", nullable = false)
        var fromAddress: String,

        @Column(name = "to_address", nullable = false)
        var toAddress: String,

        @Column(name = "date", nullable = false)
        var date: LocalDateTime,

        @Column(name = "block_number", nullable = false)
        var blockNumber: Long,

        @Column(name = "blockchain_type_id", nullable = false)
        var blockchainTypeId: Int

) : BaseModel()
