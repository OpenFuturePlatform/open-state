package io.openfuture.state.service

import io.openfuture.state.entity.WebHook
import io.openfuture.state.repository.WebHookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultWebHookService(
        private val repository: WebHookRepository
) : WebHookService {

    @Transactional
    override fun save(webHook: WebHook): WebHook {
        return repository.save(webHook)
    }

}
