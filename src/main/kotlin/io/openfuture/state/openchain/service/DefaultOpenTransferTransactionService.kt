package io.openfuture.state.openchain.service

import io.openfuture.state.openchain.entity.OpenTransferTransaction
import io.openfuture.state.openchain.repository.OpenTransferTransactionRepository
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultOpenTransferTransactionService(
        private val openTransferTransactionRepository: OpenTransferTransactionRepository
) : OpenTransferTransactionService {

    @Transactional
    override suspend fun save(openTransferTransaction: OpenTransferTransaction): OpenTransferTransaction {
        return openTransferTransactionRepository.save(openTransferTransaction).awaitSingle()
    }
}