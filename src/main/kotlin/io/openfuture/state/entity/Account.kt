package io.openfuture.state.entity

import io.openfuture.state.entity.base.BaseModel
import javax.persistence.*

@Entity
@Table(name = "accounts")
class Account(

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

) : BaseModel() {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false
                if (!super.equals(other)) return false

                other as Account

                if (webHook != other.webHook) return false
                if (isEnabled != other.isEnabled) return false
                if (wallets != other.wallets) return false

                return true
        }

        override fun hashCode(): Int {
                var result = super.hashCode()
                result = 31 * result + webHook.hashCode()
                result = 31 * result + isEnabled.hashCode()
                result = 31 * result + wallets.hashCode()
                return result
        }
}
