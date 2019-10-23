package io.openfuture.state.openchain.entity

import io.openfuture.state.entity.base.BaseModel
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "open_transfer_transactions")
class OpenTransferTransaction(

        @Column(name = "fee")
        var fee: Long,

        @Column(name = "amount", nullable = false)
        var amount: Long,

        @Column(name = "hash", nullable = false)
        var hash: String,

        @Column(name = "sender_address", nullable = false)
        var senderAddress: String,

        @Column(name = "recipient_address", nullable = false)
        var recipientAddress: String,

        @Column(name = "block_hash", nullable = false)
        var blockHash: String,

        @Column(name = "date", nullable = false)
        var date: Long,

        @Column(name = "web_hook", nullable = false)
        var webHook: String

) : BaseModel()
