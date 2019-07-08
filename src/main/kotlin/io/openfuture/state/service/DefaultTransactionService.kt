package io.openfuture.state.service

import io.openfuture.state.entity.Transaction
import io.openfuture.state.repository.TransactionRepository
import javassist.NotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultTransactionService(
        private val repository: TransactionRepository
) : TransactionService {

    @Transactional
    override fun save(transaction: Transaction): Transaction {
        return repository.save(transaction)
    }

    @Transactional(readOnly = true)
    override fun get(id: Long, walletId: Long): Transaction {
        return repository.findByIdAndWalletId(id, walletId) ?: throw NotFoundException("Transaction with id $id not found")
    }

    @Transactional(readOnly = true)
    override fun getAllByWalletId(walletId: Long, pageable: Pageable): Page<Transaction> {
        return repository.findAllByWalletIdOrderByDateDesc(walletId, pageable)
    }

}
