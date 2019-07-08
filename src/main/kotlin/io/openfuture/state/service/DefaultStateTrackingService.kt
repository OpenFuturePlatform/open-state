package io.openfuture.state.service

import io.openfuture.state.domain.dto.TransactionDto
import io.openfuture.state.entity.Transaction
import io.openfuture.state.entity.TransactionType
import io.openfuture.state.entity.Wallet
import io.openfuture.state.util.HashUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
class DefaultStateTrackingService(
        private val stateService: StateService,
        private val walletService: WalletService,
        private val transactionService: TransactionService
) : StateTrackingService {

    @Transactional
    override fun processTransaction(tx: TransactionDto) {

        val fromWallet = walletService.getByBlockchainAddress(tx.blockchainId, tx.from)

        if (fromWallet != null) {
            val outputTransaction = saveTransaction(fromWallet, TransactionType.OUTPUT, tx.to, tx)
            updateState(fromWallet.state.id, outputTransaction.amount.unaryMinus())
        }

        val toWallet = walletService.getByBlockchainAddress(tx.blockchainId, tx.to)

        if (toWallet != null) {
            val inputTransaction = saveTransaction(toWallet, TransactionType.INPUT, tx.from, tx)
            updateState(toWallet.id, inputTransaction.amount)
        }

    }

    private fun updateState(stateId: Long, amount: Long) {
        val state = stateService.get(stateId)
        state.balance += amount
        state.date = Date().time
        state.root = calculateRootHash()

        stateService.save(state)
    }

    private fun saveTransaction(wallet: Wallet, type: TransactionType, participant: String, tx: TransactionDto): Transaction {
        val hash = Transaction.generateHash(wallet.address, type.getId(), participant, tx.amount, tx.date)
        val transaction = Transaction(wallet, hash, tx.hash, type.getId(), participant, tx.amount, tx.date, tx.blockHeight, tx.blockHash)

        return transactionService.save(transaction)
    }

    private fun calculateRootHash(): String {
        return "hash"
    }

}
