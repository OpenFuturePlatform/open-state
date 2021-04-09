package io.openfuture.state.service

import io.openfuture.state.domain.*
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.extensions.toMillisDouble
import io.openfuture.state.repository.WebhookQueueRedisRepository
import io.openfuture.state.util.MathUtil
import io.openfuture.state.util.toEpochMilli
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

@Service
class DefaultWebhookService(
    private val repository: WebhookQueueRedisRepository
) : WebhookService {

    override suspend fun scheduleTransaction(wallet: Wallet, transaction: Transaction) {
        val transactionTask = TransactionQueueTask(transaction.id, transaction.date)

        if (isQueued(wallet.id)) {
            repository.addTransaction(wallet.id, transactionTask)
        } else {
            repository.addWallet(wallet.id, transactionTask, transactionTask.timestamp.toEpochMilli().toDouble())
        }
    }

    override suspend fun firstWalletInQueue(score: Double?): WalletQueueTask? {
        val walletId = repository.firstWalletInScoreRange(score, LocalDateTime.now().toEpochMilli().toDouble())
        if (walletId != null) {
            val walletScore = repository.walletScore(walletId)
            return WalletQueueTask(walletId, walletScore)
        }

        return null
    }

    override suspend fun firstTransaction(walletId: String): TransactionQueueTask {
        return repository.firstTransaction(walletId) ?: throw NotFoundException("Transaction not found")
    }

    override suspend fun lock(walletId: String): Boolean {
        return repository.lock(walletId)
    }

    override suspend fun unlock(walletId: String) {
        repository.unlock(walletId)
    }

    private suspend fun isQueued(walletId: String): Boolean {
        return repository.walletScore(walletId) != null
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
