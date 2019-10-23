package io.openfuture.state.openchain.service

import io.openfuture.state.openchain.entity.OpenTransferTransaction
import io.openfuture.state.openchain.repository.OpenTransferTransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultOpenTransferTransactionService(
        private val repository: OpenTransferTransactionRepository
) : OpenTransferTransactionService {

    @Transactional(readOnly = true)
    override fun findByHashes(hashes: List<String>): List<OpenTransferTransaction>? {
        return repository.findByHashIn(hashes)
    }
}