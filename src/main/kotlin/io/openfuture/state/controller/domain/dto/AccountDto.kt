package io.openfuture.state.controller.domain.dto

import io.openfuture.state.entity.Account

data class AccountDto(
        val id: Long,
        val webHook: String,
        val isEnabled: Boolean,
        val walletsCount: Int
) {

    constructor(account: Account) : this(
            account.id,
            account.webHook,
            account.isEnabled,
            account.wallets.size
    )

}
