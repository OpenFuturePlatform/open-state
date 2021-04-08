package io.openfuture.state.service

import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.TransactionQueueTask
import io.openfuture.state.domain.Wallet
import org.springframework.stereotype.Service
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

}
