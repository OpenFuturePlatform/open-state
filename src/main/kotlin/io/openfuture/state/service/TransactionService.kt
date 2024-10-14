package io.openfuture.state.service

import io.openfuture.state.domain.transaction.Transaction

interface TransactionService {

    suspend fun findById(id: String): Transaction

    suspend fun findByAddress(address: String): List<Transaction>

    suspend fun findAll(): List<Transaction>
}
