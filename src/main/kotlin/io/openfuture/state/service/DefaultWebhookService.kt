package io.openfuture.state.service

import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.TransactionQueueTask
import io.openfuture.state.domain.Wallet
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class DefaultWebhookService(
        private val walletQueueService: WalletQueueService,
        private val transactionsQueueService: TransactionsQueueService
): WebhookService {

    override suspend fun scheduleTransaction(wallet: Wallet, transaction: Transaction) {
        val transactionTask = TransactionQueueTask(transaction.id)

        val score = walletQueueService.score(wallet.id)
        if (score == null) {
            walletQueueService.add(wallet.id, transactionTask)
        } else {
            transactionsQueueService.add(wallet.id, transactionTask)
        }
    }

    override suspend fun walletsScheduledForNow(): List<String> {
        return walletQueueService.walletsScheduledTo(LocalDateTime.now())
    }

    override suspend fun lock(walletId: String): Boolean {
        return walletQueueService.lock(walletId)
    }

    override suspend fun unlock(walletId: String) {
        walletQueueService.unlock(walletId)
    }
}
