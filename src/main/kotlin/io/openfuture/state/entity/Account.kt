package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
import javax.persistence.*

@Entity
@Table(name = "accounts")
data class Account(

        @Column(name = "web_hook", nullable = false)
        var webHook: String,

        @Column(name = "is_enabled", nullable = false)
        var isEnabled: Boolean = true,

        @ManyToMany
        @JoinTable(
                name = "accounts2wallets",
                joinColumns = [JoinColumn(name = "account_id")],
                inverseJoinColumns = [JoinColumn(name = "wallet_id")]
        )
        var wallets: MutableSet<Wallet> = mutableSetOf()

) : BaseModel()