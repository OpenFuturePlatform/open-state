package io.openfuture.state.service.transaction

import io.openfuture.state.entity.Transaction
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.transaction.TransactionRepository
import io.openfuture.state.service.transaction.TransactionService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class DefaultTransactionService(
        private val repository: TransactionRepository
) : TransactionService {

    override suspend fun save(transaction: Transaction): Transaction {
        return repository.save(transaction).awaitSingle()
    }

    override suspend fun get(id: String, walledAddress: String): Transaction {
        return repository.findByIdAndWalletAddress(id, walledAddress)
                .awaitSingle()
                ?: throw NotFoundException("Transaction with id $id not found")
    }

    override suspend fun getAllByWalletAddress(walledAddress: String, pageable: Pageable): Page<Transaction> = coroutineScope {
        val transactions = async {
            repository.findAllByWalletAddressOrderByDateDesc(walledAddress, pageable).asFlow().toList()
        }
        val count = async {
            repository.countByWalletAddress(walledAddress).awaitSingle()
        }

        PageImpl<Transaction>(transactions.await(), pageable, count.await())
    }
}
