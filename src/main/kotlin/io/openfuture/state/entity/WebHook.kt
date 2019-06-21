package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "web_hooks")
class WebHook(

        @Column(name = "url", nullable = false)
        var url: String,

        @Column(name = "is_enabled", nullable = false)
        var isEnabled: Boolean = true

) : BaseModel()
