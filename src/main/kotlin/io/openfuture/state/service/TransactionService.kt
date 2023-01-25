package io.openfuture.state.service

import io.openfuture.state.domain.Transaction
import reactor.core.publisher.Flux

interface TransactionService {

    suspend fun findById(id: String): Transaction

    suspend fun findByAddress(address: String): List<Transaction>

    suspend fun findAll(): List<Transaction>
}
