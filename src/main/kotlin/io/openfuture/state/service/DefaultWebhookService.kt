package io.openfuture.state.service

import io.openfuture.state.config.WebhookConfig
import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.util.MathUtils
import io.openfuture.state.util.toEpochMilli
import io.openfuture.state.webhook.ScheduledTransaction
import io.openfuture.state.webhook.WebhookStatus
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import kotlin.streams.toList

@Service
class DefaultWebhookService(
        private val walletService: WalletWebhookQueueService,
        private val transactionService: TransactionsQueueService,
        private val deadQueueService: WebhookDeadQueueService,
        private val webhookConfig: WebhookConfig
): WebhookService {

    override suspend fun addTransaction(wallet: Wallet, transaction: Transaction) {
        if (wallet.webhookStatus == WebhookStatus.FAILED) {
            deadQueueService.addTransactions(
                    wallet.walletKey(),
                    listOf(ScheduledTransaction(transaction.hash))
            )

            return
        }

        val scheduledTransaction = ScheduledTransaction(transaction.hash)
        val score = walletService.score(wallet.walletKey())
        if (score == null) {
            walletService.add(wallet.walletKey(), scheduledTransaction)
        } else {
            transactionService.add(wallet.walletKey(), scheduledTransaction)
        }
    }

    override suspend fun addTransactionsFromDeadQueue(wallet: Wallet) {
        if (!deadQueueService.hasTransactions(wallet.walletKey())) {
            return
        }

        val deadTransactions = deadQueueService.getTransactions(wallet.walletKey())
        val score = walletService.score(wallet.walletKey())
        if (score == null) {
            walletService.add(wallet.walletKey(), deadTransactions[0])
        }

        val skip = if (score == null) 1L else 0L
        for (transaction in deadTransactions.stream().skip(skip).toList()) {
            transactionService.add(wallet.walletKey(), transaction)
        }

        deadQueueService.remove(wallet.walletKey())
    }

    override suspend fun scheduleNextWebhook(wallet: Wallet) {
        if (wallet.webhookStatus == WebhookStatus.FAILED) {
            return
        }

        if (transactionService.hasTransactions(wallet.walletKey())) {
            val score = walletService.score(wallet.walletKey())
                    ?: throw NotFoundException("Wallet not found")

            val nextTransaction = firstTransaction(wallet)
            val scoreDiff = nextTransaction.timestamp.toEpochMilli().toDouble() - score

            walletService.incrementScore(wallet.walletKey(), scoreDiff)
            transactionService.setAt(
                    wallet.walletKey(),
                    nextTransaction,
                    0
            )
        }
        else {
            cancelSchedule(wallet.walletKey())
        }
    }

    override suspend fun scheduleFailedWebhook(wallet: Wallet, transaction: ScheduledTransaction) {
        if (transaction.attempts >= webhookConfig.maxAttempts()) {
            val transactions = transactionService.findAll(wallet.walletKey())

            val deadTransactions = mutableListOf(transaction)
            deadTransactions.addAll(transactions)

            deadQueueService.addTransactions(wallet.walletKey(), deadTransactions)

            cancelSchedule(wallet.walletKey())
            return
        }

        walletService.incrementScore(
                wallet.walletKey(),
                buildInvocationDelay(transaction.attempts)
        )
        transactionService.setAt(wallet.walletKey(), transaction.apply { attempts++ }, 0)
    }

    override suspend fun scheduledWallets(): List<String> {
        return walletService.walletsScheduledTo(LocalDateTime.now())
    }

    override suspend fun firstTransaction(wallet: Wallet): ScheduledTransaction {
        return transactionService.first(wallet.walletKey())
    }

    override suspend fun lock(walletKey: String): Boolean {
        return walletService.lock(walletKey)
    }

    override suspend fun unlock(walletKey: String) {
        walletService.unlock(walletKey)
    }

    private suspend fun cancelSchedule(walletKey: String) {
        walletService.remove(walletKey)
        transactionService.remove(walletKey)
    }

    private fun buildInvocationDelay(attempt: Int): Double {
        val delay = if (attempt <= webhookConfig.progressiveAttempts())
            Duration.ofSeconds(MathUtils.fibonachi(attempt))
        else
            Duration.ofDays(1)

        return delay.toMillis().toDouble()
    }
}
