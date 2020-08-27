package io.openfuture.state.mapper

import io.openfuture.state.controller.domain.dto.WalletDto
import io.openfuture.state.model.Wallet

interface WalletMapper {
    fun toWalletDto(wallet: Wallet): WalletDto
}
