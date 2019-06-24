package io.openfuture.state.service

import io.openfuture.state.entity.State
import io.openfuture.state.entity.Wallet
import io.openfuture.state.repository.StateRepository
import org.springframework.stereotype.Service

@Service
class DefaultStateService(
        private val repository: StateRepository
) : StateService {

    override fun getByWalletId(wallet: Wallet): State {
        return repository.findByWallet(wallet)
    }

    override fun update(state: State) {
        repository.save(state)
    }

}
