package io.openfuture.state.openchain.service

import io.openfuture.state.openchain.entity.OpenTrackingLog
import io.openfuture.state.openchain.repository.OpenTrackingLogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultOpenTrackingLogService(
        private val repository: OpenTrackingLogRepository
) : OpenTrackingLogService {

    @Transactional(readOnly = true)
    override fun getLastOpenTrackingLog(): OpenTrackingLog? {
        return repository.findFirstByOrderByIdDesc()
    }

    @Transactional
    override fun save(offset: Long, hash: String): OpenTrackingLog {
        val transaction = OpenTrackingLog(offset, hash)

        return repository.save(transaction)
    }

}