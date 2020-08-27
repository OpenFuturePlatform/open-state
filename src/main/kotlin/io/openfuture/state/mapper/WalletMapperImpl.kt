package io.openfuture.state.mapper

import io.openfuture.state.controller.domain.dto.WalletDto
import io.openfuture.state.model.Wallet
import org.springframework.stereotype.Component

@Component
class WalletMapperImpl : WalletMapper {
    override fun toWalletDto(wallet: Wallet): WalletDto {
        return WalletDto(
                id = wallet.id.toString(),
                address = wallet.address,
                webhook = wallet.webhook,
                lastUpdateDate = wallet.lastUpdateDate
        )
    }

}
