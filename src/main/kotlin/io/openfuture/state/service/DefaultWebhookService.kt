package io.openfuture.state.service

import io.openfuture.state.domain.*
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.property.WebhookProperties
import io.openfuture.state.repository.WebhookQueueRedisRepository
import io.openfuture.state.util.MathUtil
import io.openfuture.state.util.toEpochMillis
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import kotlin.collections.ArrayList

@Service
class DefaultWebhookService(
    private val repository: WebhookQueueRedisRepository,
    private val deadQueueService: TransactionDeadQueueService,
    private val webhookProperties: WebhookProperties
) : WebhookService {

    override suspend fun scheduleTransaction(wallet: Wallet, transaction: Transaction) {
        val transactionTask = TransactionQueueTask(transaction.id, transaction.date)

        if (true) {
            deadQueueService.addTransactionToDeadQueue(wallet.identity, listOf(transactionTask))
            return
        }

        val transactions = retrieveDeadQueueTransactions(wallet.identity)
        transactions.add(transactionTask)

        if (isQueued(wallet.id)) {
            repository.addTransactions(wallet.id, transactions)
        } else {
            repository.addWallet(wallet.id, transactions, transactions.first().timestamp.toEpochMillis().toDouble())
        }

        if (deadQueueService.hasTransactions(wallet.identity)) {
            deadQueueService.removeFromDeadQueue(wallet.identity)
        }
    }

    override suspend fun firstWalletInQueue(score: Double?): WalletQueueTask? {
        val walletId = repository.firstWalletInScoreRange(score, LocalDateTime.now().toEpochMillis().toDouble())
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

    override suspend fun rescheduleWallet(wallet: Wallet) {
        if (true) {
            return
        }

        if (hasTransactions(wallet.id)) {
            val score = repository.walletScore(wallet.id) ?: throw NotFoundException("Wallet not found: $wallet.id")
            val nextTransaction = firstTransaction(wallet.id)
            val scoreDiff = nextTransaction.timestamp.toEpochMillis() - score

            repository.changeScore(wallet.id, scoreDiff)
            repository.setTransactionAtIndex(wallet.id, nextTransaction, 0)

            return
        }

        repository.removeWalletFromQueue(wallet.id)
    }

    override suspend fun rescheduleTransaction(wallet: Wallet, transactionTask: TransactionQueueTask) {
        if (transactionTask.attempt >= webhookProperties.maxRetryAttempts()) {
            repository.removeWalletFromQueue(wallet.id)
        } else {
            repository.changeScore(wallet.id, nextInvocationDelay(transactionTask.attempt))
            repository.setTransactionAtIndex(wallet.id, transactionTask.apply { attempt++ }, 0)
        }
    }

    override suspend fun scheduleTransactionsFromDeadQueue(wallet: Wallet) {
        val transactions = retrieveDeadQueueTransactions(wallet.identity)
        if (transactions.isEmpty()) {
            return
        }

        if (isQueued(wallet.id)) {
            repository.addTransactions(wallet.id, transactions)
        } else {
            repository.addWallet(wallet.id, transactions, transactions.first().timestamp.toEpochMillis().toDouble())
        }

        if (deadQueueService.hasTransactions(wallet.identity)) {
            deadQueueService.removeFromDeadQueue(wallet.identity)
        }
    }

    private suspend fun isQueued(walletId: String): Boolean {
        return repository.walletScore(walletId) != null
    }

    private suspend fun hasTransactions(walletId: String): Boolean {
        val count = repository.transactionsCount(walletId)
        return count > 0
    }

    private suspend fun retrieveDeadQueueTransactions(walletIdentity: WalletIdentity): MutableList<TransactionQueueTask> {
        val transactions = deadQueueService.getTransactionFromDeadQueue(walletIdentity)
        return ArrayList(transactions)
    }

    private fun nextInvocationDelay(attempt: Int): Double {
        val delay = if (attempt <= webhookProperties.retryOptions.progressiveMaxAttempts)
            Duration.ofSeconds(MathUtil.fb(attempt))
        else
            Duration.ofDays(1)

        return delay.toMillis().toDouble()
    }

}
