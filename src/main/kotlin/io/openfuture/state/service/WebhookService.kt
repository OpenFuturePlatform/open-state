package io.openfuture.state.service

import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet

interface WebhookService {

    suspend fun scheduleTransaction(wallet: Wallet, transaction: Transaction)
}
