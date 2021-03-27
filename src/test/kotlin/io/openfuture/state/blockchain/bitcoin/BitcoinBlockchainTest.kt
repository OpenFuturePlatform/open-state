package io.openfuture.state.blockchain.bitcoin

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import io.openfuture.state.util.createDummyBitcoinBlock
import io.openfuture.state.util.createDummyUnifiedBlock
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BitcoinBlockchainTest {

    private val rpcClient: BitcoinRpcClient = mock()

    private lateinit var bitcoinBlockchain: BitcoinBlockchain


    @BeforeEach
    fun setUp() {
        bitcoinBlockchain = BitcoinBlockchain(rpcClient)
    }


    @Test
    fun getLastBlockNumberShouldReturnExpectedValue() = runBlocking<Unit> {
        val hash = "0000000000015afb856ff92b062a4d023d104f7f1850914d9288d1bb889ffec3"

        given(rpcClient.getLatestBlockHash()).willReturn(hash)
        given(rpcClient.getBlockHeight(hash)).willReturn(5)

        val result = bitcoinBlockchain.getLastBlockNumber()

        Assertions.assertThat(result).isEqualTo(5)
    }

    @Test
    fun getBlockShouldReturnExpectedValue() = runBlocking<Unit> {
        val hash = "hash"

        val block = createDummyBitcoinBlock()
        val expected = createDummyUnifiedBlock()

        given(rpcClient.getBlockHash(1)).willReturn(hash)
        given(rpcClient.getBlock(hash)).willReturn(block)
        given(rpcClient.getInputAddress("id", 1)).willReturn("from")

        val result = bitcoinBlockchain.getBlock(1)

        Assertions.assertThat(result).isEqualTo(expected)
    }

}
