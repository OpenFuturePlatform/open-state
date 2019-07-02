package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "accounts")
class Account(

        @Column(name = "web_hook", nullable = false)
        var webhook: String,

        @Column(name = "is_enabled", nullable = false)
        var isEnabled: Boolean = true

) : BaseModel()
