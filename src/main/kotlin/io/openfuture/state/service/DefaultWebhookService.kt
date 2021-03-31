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
        private val walletService: WalletQueueService,
        private val transactionService: TransactionsQueueService,
        private val deadQueueService: WebhookDeadQueueService,
        private val webhookConfig: WebhookConfig
): WebhookService {

    override suspend fun addTransaction(wallet: Wallet, transaction: Transaction) {
        if (wallet.webhookStatus == WebhookStatus.FAILED) {
            deadQueueService.addTransactions(
                    wallet.address,
                    listOf(ScheduledTransaction(transaction.id))
            )

            return
        }

        val scheduledTransaction = ScheduledTransaction(transaction.id)
        val score = walletService.score(wallet.id)
        if (score == null) {
            walletService.add(wallet.id, scheduledTransaction)
        } else {
            transactionService.add(wallet.id, scheduledTransaction)
        }
    }

    override suspend fun addTransactionsFromDeadQueue(wallet: Wallet) {
        if (!deadQueueService.hasTransactions(wallet.address)) {
            return
        }

        val deadTransactions = deadQueueService.getTransactions(wallet.address)
        val score = walletService.score(wallet.id)
        if (score == null) {
            walletService.add(wallet.id, deadTransactions[0])
        }

        val skip = if (score == null) 1L else 0L
        for (transaction in deadTransactions.stream().skip(skip).toList()) {
            transactionService.add(wallet.id, transaction)
        }

        deadQueueService.remove(wallet.address)
    }

    override suspend fun scheduleNextWebhook(wallet: Wallet) {
        if (wallet.webhookStatus == WebhookStatus.FAILED) {
            return
        }

        if (transactionService.hasTransactions(wallet.id)) {
            val score = walletService.score(wallet.id)
                    ?: throw NotFoundException("Wallet not found")

            val nextTransaction = firstTransaction(wallet)
            val scoreDiff = nextTransaction.timestamp.toEpochMilli().toDouble() - score

            walletService.incrementScore(wallet.id, scoreDiff)
            transactionService.setAt(
                    wallet.id,
                    nextTransaction,
                    0
            )
        }
        else {
            cancelSchedule(wallet.id)
        }
    }

    override suspend fun scheduleFailedWebhook(wallet: Wallet, transaction: ScheduledTransaction) {
        if (transaction.attempts >= webhookConfig.maxAttempts()) {
            val transactions = transactionService.findAll(wallet.id)

            val deadTransactions = mutableListOf(transaction)
            deadTransactions.addAll(transactions)

            deadQueueService.addTransactions(wallet.address, deadTransactions)

            cancelSchedule(wallet.id)
            return
        }

        walletService.incrementScore(
                wallet.id,
                buildInvocationDelay(transaction.attempts)
        )
        transactionService.setAt(wallet.id, transaction.apply { attempts++ }, 0)
    }

    override suspend fun scheduledWallets(): List<String> {
        return walletService.walletsScheduledTo(LocalDateTime.now())
    }

    override suspend fun firstTransaction(wallet: Wallet): ScheduledTransaction {
        return transactionService.first(wallet.id)
    }

    override suspend fun lock(walletId: String): Boolean {
        return walletService.lock(walletId)
    }

    override suspend fun unlock(walletId: String) {
        walletService.unlock(walletId)
    }

    private suspend fun cancelSchedule(walletId: String) {
        walletService.remove(walletId)
        transactionService.remove(walletId)
    }

    private fun buildInvocationDelay(attempt: Int): Double {
        val delay = if (attempt <= webhookConfig.progressiveAttempts())
            Duration.ofSeconds(MathUtils.fibonachi(attempt))
        else
            Duration.ofDays(1)

        return delay.toMillis().toDouble()
    }
}
