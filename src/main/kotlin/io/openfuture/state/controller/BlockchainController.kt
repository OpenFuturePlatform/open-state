package io.openfuture.state.controller

import io.openfuture.state.domain.dto.BlockchainDto
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
    fun getAll(): List<BlockchainDto> {
        val blockchains = blockchainService.getAll()
        return blockchains.map { BlockchainDto(it) }
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): BlockchainDto {
        val blockchain = blockchainService.get(id)
        return BlockchainDto(blockchain)
    }

}
