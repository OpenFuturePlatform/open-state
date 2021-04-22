package io.openfuture.state.service

import io.openfuture.state.domain.Transaction

interface TransactionService {

    suspend fun findById(id: String): Transaction
}
