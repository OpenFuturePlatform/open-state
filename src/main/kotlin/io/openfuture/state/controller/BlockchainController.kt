package io.openfuture.state.controller

import io.openfuture.state.entity.Blockchain
import io.openfuture.state.service.BlockchainService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/blockchains")
class BlockchainController(
        private val blockchainService: BlockchainService
) {

    @GetMapping
    fun getAll(): List<Blockchain> {
        return blockchainService.getAll()
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): Blockchain {
        return blockchainService.get(id)
    }

}
