package io.openfuture.state.service

import io.openfuture.state.webhook.WebhookSender
import io.openfuture.state.domain.dto.TransactionDto
import io.openfuture.state.entity.State
import io.openfuture.state.entity.Transaction
import io.openfuture.state.entity.TransactionType
import io.openfuture.state.entity.Wallet
import io.openfuture.state.util.HashUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class DefaultStateTrackingService(
        private val stateService: StateService,
        private val walletService: WalletService,
        private val transactionService: TransactionService,
        private val webHookSender: WebhookSender
) : StateTrackingService {

    @Transactional
    override fun processTransaction(tx: TransactionDto) {

        val fromWallet = walletService.getActiveByBlockchainAddress(tx.blockchainId, tx.from)

        if (null != fromWallet) {
            val outputTransaction = saveTransaction(fromWallet, TransactionType.OUTPUT, tx.to, tx)
            updateState(fromWallet.address, fromWallet.state.id, outputTransaction.amount.unaryMinus())
        }

        val toWallet = walletService.getActiveByBlockchainAddress(tx.blockchainId, tx.to)

        if (null != toWallet) {
            val inputTransaction = saveTransaction(toWallet, TransactionType.INPUT, tx.from, tx)
            updateState(toWallet.address, toWallet.id, inputTransaction.amount)
        }

    }

    private fun updateState(walletAddress: String, stateId: Long, amount: Long) {
        val state = stateService.get(stateId)
        state.balance += amount
        state.date = Date().time

        val hash = State.generateHash(walletAddress, state.balance, state.date)
        state.root = HashUtils.merkleRoot(listOf(state.root, hash))

        stateService.save(state)
    }

    private fun saveTransaction(wallet: Wallet, type: TransactionType, participant: String, tx: TransactionDto): Transaction {
        val hash = Transaction.generateHash(wallet.address, type.getId(), participant, tx.amount, tx.date)
        val transaction = Transaction(wallet, hash, tx.hash, type.getId(), participant, tx.amount, tx.date, tx.blockHeight, tx.blockHash)

        val savedTransaction = transactionService.save(transaction)

        //send web hook
        webHookSender.sendWebHook(wallet.accounts.map { it.webHook }, savedTransaction)

        return savedTransaction
    }

}
