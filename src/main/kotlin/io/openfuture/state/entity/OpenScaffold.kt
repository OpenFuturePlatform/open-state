package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "open_scaffolds")
class OpenScaffold(

        @Column(name = "recipient_address", nullable = false, unique = true)
        var recipientAddress: String,

        @Column(name = "web_hook", nullable = false)
        var webHook: String
) : BaseModel()