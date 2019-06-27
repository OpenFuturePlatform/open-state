package io.openfuture.state.service

import io.openfuture.state.entity.State
import io.openfuture.state.entity.Wallet
import io.openfuture.state.repository.StateRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultStateService(
        private val repository: StateRepository
) : StateService {

    @Transactional(readOnly = true)
    override fun getByWalletId(walletId: Long): State {
        return repository.findByWalletId(walletId)
    }

    @Transactional
    override fun save(state: State) {
        repository.save(state)
    }

}
