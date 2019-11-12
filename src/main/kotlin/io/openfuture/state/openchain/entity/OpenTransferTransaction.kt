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

        @Column(name = "amount")
        var amount: Long,

        @Column(name = "hash")
        var hash: String,

        @Column(name = "sender_address")
        var senderAddress: String,

        @Column(name = "recipient_address")
        var recipientAddress: String?,

        @Column(name = "block_hash")
        var blockHash: String,

        @Column(name = "date")
        var date: Long,

        @Column(name = "web_hook")
        var webHook: String?

) : BaseModel()
