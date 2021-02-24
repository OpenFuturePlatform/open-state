package io.openfuture.state.service

import io.openfuture.state.domain.Transaction

interface TransactionService {

    suspend fun findByHash(hash: String): Transaction

    suspend fun save(transaction: Transaction): Transaction
}
