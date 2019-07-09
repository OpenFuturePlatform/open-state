package io.openfuture.state.service

import io.openfuture.state.entity.State
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.StateRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultStateService(
        private val repository: StateRepository
) : StateService {

    @Transactional
    override fun save(state: State): State {
        return repository.save(state)
    }

    @Transactional(readOnly = true)
    override fun get(id: Long): State {
        return repository.findById(id).orElseThrow { throw NotFoundException("State with id $id not found") }
    }

}
