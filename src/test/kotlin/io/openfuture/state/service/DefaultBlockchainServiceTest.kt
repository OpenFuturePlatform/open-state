package io.openfuture.state.service

import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.BlockchainRepository
import io.openfuture.state.util.createDummyBlockchain
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import java.util.*

class DefaultBlockchainServiceTest {

    private val repository: BlockchainRepository = mock(BlockchainRepository::class.java)

    private lateinit var blockchainService: BlockchainService


    @Before
    fun setUp() {
        blockchainService = DefaultBlockchainService(repository)
    }

    @Test
    fun getShouldReturnBlockchainWhenExists() {
        val blockchain = createDummyBlockchain().apply { id = 1 }

        given(repository.findById(blockchain.id)).willReturn(Optional.of(blockchain))

        val result = blockchainService.get(blockchain.id)

        assertThat(result).isEqualTo(blockchain)
    }

    @Test(expected = NotFoundException::class)
    fun getShouldThrowExceptionWhenBlockchainDoesNotExists() {
        val blockchain = createDummyBlockchain().apply { id = 1 }

        given(repository.findById(blockchain.id)).willReturn(Optional.empty())

        blockchainService.get(blockchain.id)
    }

    @Test
    fun getAll() {
        val blockchain = createDummyBlockchain()

        given(repository.findAll()).willReturn(listOf(blockchain))

        val result = blockchainService.getAll()

        assertThat(result).contains(blockchain)
    }

}
