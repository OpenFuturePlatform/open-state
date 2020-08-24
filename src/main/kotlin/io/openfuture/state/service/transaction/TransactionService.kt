package io.openfuture.state.service.transaction

import io.openfuture.state.entity.Transaction
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface TransactionService {

    suspend fun save(transaction: Transaction): Transaction

    suspend fun get(id: String, walledAddress: String): Transaction

    suspend fun getAllByWalletAddress(walledAddress: String, pageable: Pageable): Page<Transaction>

}