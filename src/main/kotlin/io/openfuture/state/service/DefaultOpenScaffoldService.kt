package io.openfuture.state.service

import io.openfuture.state.controller.domain.request.SaveOpenScaffoldRequest
import io.openfuture.state.entity.OpenScaffold
import io.openfuture.state.exception.DuplicateEntityException
import io.openfuture.state.repository.ScaffoldRepository
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultOpenScaffoldService(
        private val repository: ScaffoldRepository
) : OpenScaffoldService {

    @Transactional
    override suspend fun save(request: SaveOpenScaffoldRequest): OpenScaffold {
        repository.findByRecipientAddress(request.address).awaitFirst()?.let {
            throw DuplicateEntityException("OpenScaffold already exists with an address ".plus(request.address))
        }
        return repository.save(OpenScaffold(recipientAddress = request.address, webHook = request.webHook)).awaitFirst()
    }

    @Transactional(readOnly = true)
    override suspend fun findByRecipientAddress(addresses: String): OpenScaffold? {
        return repository.findByRecipientAddress(addresses).awaitFirst()
    }
}