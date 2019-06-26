package io.openfuture.state.service

import io.openfuture.state.domain.dto.TransactionDto
import io.openfuture.state.entity.Transaction
import io.openfuture.state.entity.TransactionType
import io.openfuture.state.entity.Wallet
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

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
            updateState(fromWallet, -outputTransaction.amount)
        }

        val toWallet = walletService.getByBlockchainAddress(tx.blockchainId, tx.to)

        if (toWallet != null) {
            val inputTransaction = saveTransaction(toWallet, TransactionType.INPUT, tx.from, tx)
            updateState(toWallet, inputTransaction.amount)
        }

    }

    private fun updateState(wallet: Wallet, amount: Long) {
        val state = stateService.getByWalletId(wallet)
        state.balance += amount
        state.date = LocalDateTime.now()
        state.root = calculateRootHash()

        stateService.save(state)
    }

    private fun saveTransaction(wallet: Wallet, type: TransactionType, participant: String, tx: TransactionDto): Transaction {
        val hash = calculateHash(tx)
        val transaction = Transaction(wallet, tx.hash, hash, type.getId(), participant, tx.amount, tx.date, tx.blockHeight, tx.blockHash)

        return transactionService.save(transaction)
    }

    private fun calculateHash(tx: TransactionDto): String {
        return "hash"
    }

    private fun calculateRootHash(): String {
        return "hash"
    }

}
