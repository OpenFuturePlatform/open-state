package io.openfuture.state.service

import io.openfuture.state.entity.Blockchain
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.BlockchainRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultBlockchainService(
        private val repository: BlockchainRepository
) : BlockchainService {

    @Transactional(readOnly = true)
    override fun get(id: Long): Blockchain {
        return repository.findById(id).orElseThrow { throw NotFoundException("Blockchain with id $id not found") }
    }

    @Transactional(readOnly = true)
    override fun getAll(): List<Blockchain> {
        return repository.findAll()
    }

}
