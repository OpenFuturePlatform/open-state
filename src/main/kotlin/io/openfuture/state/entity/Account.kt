package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "accounts")
class Account(

        @Column(name = "webhook", nullable = true)
        var webhook: String,

        @Column(name = "disabled", nullable = false)
        var disabled: Boolean = false


) : BaseModel()