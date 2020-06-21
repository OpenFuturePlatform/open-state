package io.openfuture.state.controller

import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.service.BlockchainService
import io.openfuture.state.util.createDummyBlockchain
import org.junit.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(BlockchainController::class)
class BlockchainControllerTest : BaseControllerTest() {

    @MockBean
    private lateinit var blockchainService: BlockchainService


    @Test
    fun getAllBlockchainsTest() {
        val blockchain = createDummyBlockchain().apply { id = 1 }

        given(blockchainService.getAll()).willReturn(listOf(blockchain))

        mockMvc.perform(MockMvcRequestBuilders.get("/api/blockchains"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun getBlockchainByIdTest() {
        val blockchain = createDummyBlockchain().apply { id = 1 }

        given(blockchainService.get(blockchain.id)).willReturn(blockchain)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/blockchains/1"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun get_WhenBlockchainIsNotPresent_ShouldReturnNotFoundStatusCode() {
        given(blockchainService.get(1L)).willThrow(NotFoundException("Blockchain with id 1 not found"))

        mockMvc.perform(MockMvcRequestBuilders.get("/api/blockchains/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

}
