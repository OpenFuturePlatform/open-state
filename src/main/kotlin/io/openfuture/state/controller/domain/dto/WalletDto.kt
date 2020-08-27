package io.openfuture.state.controller.domain.dto

import java.time.LocalDateTime

data class WalletDto(
        val id: String,
        val address: String,
        val webhook: String,
        val lastUpdateDate: LocalDateTime
)
