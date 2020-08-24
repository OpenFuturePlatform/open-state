package io.openfuture.state.openchain.service

import io.openfuture.state.openchain.entity.OpenTrackingLog
import io.openfuture.state.openchain.repository.OpenTrackingLogRepository
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultOpenTrackingLogService(
        private val repository: OpenTrackingLogRepository
) : OpenTrackingLogService {

    @Transactional(readOnly = true)
    override suspend fun getLastOpenTrackingLog(): OpenTrackingLog? {
        return repository.findFirstByOrderByIdDesc()
    }

    @Transactional
    override suspend fun save(offset: Long, hash: String): OpenTrackingLog {
        val transaction = OpenTrackingLog(offset = offset, hash = hash)

        return repository.save(transaction).awaitSingle()
    }

}