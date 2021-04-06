package io.openfuture.state.service

import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.TransactionQueueTask
import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WebhookStatus
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.extensions.toMillisDouble
import io.openfuture.state.property.WebhookProperties
import io.openfuture.state.util.MathUtil
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

@Service
class DefaultWebhookService(
        private val walletQueueService: WalletQueueService,
        private val transactionsQueueService: TransactionsQueueService,
        private val webhookProperties: WebhookProperties
): WebhookService {

    override suspend fun scheduleTransaction(wallet: Wallet, transaction: Transaction) {
        val transactionTask = TransactionQueueTask(transaction.id, 1, transaction.date)

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

    override suspend fun firstTransaction(wallet: Wallet): TransactionQueueTask {
        return transactionsQueueService.first(wallet.id)
    }

    override suspend fun lock(walletId: String): Boolean {
        return walletQueueService.lock(walletId)
    }

    override suspend fun unlock(walletId: String) {
        walletQueueService.unlock(walletId)
    }

    override suspend fun rescheduleWallet(wallet: Wallet) {
        if (wallet.webhookStatus == WebhookStatus.FAILED) {
            return
        }

        if (!transactionsQueueService.hasTransactions(wallet.id)) {
            cancelWalletSchedule(wallet.id)
            return
        }

        val score = walletQueueService.score(wallet.id)
                ?: throw NotFoundException("Wallet not found: $wallet.id")

        val nextTransaction = firstTransaction(wallet)
        val scoreDiff = nextTransaction.timestamp.toMillisDouble() - score

        walletQueueService.incrementScore(wallet.id, scoreDiff)
        transactionsQueueService.setAt(wallet.id, nextTransaction, 0)
    }

    override suspend fun rescheduleTransaction(wallet: Wallet, transactionTask: TransactionQueueTask) {
        if (transactionTask.attempt >= webhookProperties.maxRetryAttempts()) {
            cancelWalletSchedule(wallet.id)
        }
        else {
            walletQueueService.incrementScore(wallet.id, nextInvocationDelay(transactionTask.attempt))
            transactionsQueueService.setAt(wallet.id, transactionTask.apply { attempt++ }, 0)
        }
    }

    private suspend fun cancelWalletSchedule(walletId: String) {
        walletQueueService.remove(walletId)
        transactionsQueueService.remove(walletId)
    }

    private fun nextInvocationDelay(attempt: Int): Double {
        val delay = if (attempt <= webhookProperties.retryOptions.progressiveMaxAttempts)
            Duration.ofSeconds(MathUtil.fb(attempt))
        else
            Duration.ofDays(1)

        return delay.toMillis().toDouble()
    }
}
