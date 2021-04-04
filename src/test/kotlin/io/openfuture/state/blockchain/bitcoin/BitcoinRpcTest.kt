package io.openfuture.state.blockchain.bitcoin

import com.nhaarman.mockitokotlin2.mock
import io.openfuture.state.blockchain.bitcoin.dto.BitcoinResponse
import io.openfuture.state.blockchain.bitcoin.dto.BlockHeightBitcoinResponse
import io.openfuture.state.blockchain.bitcoin.dto.InputInfo
import io.openfuture.state.blockchain.bitcoin.dto.TransactionInputBitcoinResponse
import io.openfuture.state.util.createDummyBitcoinBlock
import io.openfuture.state.util.mockPost
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient

class BitcoinRpcTest {

    private val webClient: WebClient = mock()
    private lateinit var client: BitcoinClient

    @BeforeEach
    fun setUp() {
        client = BitcoinClient(webClient)
    }

    @Test
    fun getLatestBlockHashShouldReturnExpectedValue() = runBlocking<Unit> {
        val hash = "0000000000015afb856ff92b062a4d023d104f7f1850914d9288d1bb889ffec3"

        val response = BitcoinResponse(hash)

        mockPost(webClient, response)

        val result = client.getLatestBlockHash()

        Assertions.assertThat(result).isEqualTo(hash)
    }

    @Test
    fun getBlockHeightShouldReturnExpectedValue() = runBlocking<Unit> {
        val hash = "0000000000015afb856ff92b062a4d023d104f7f1850914d9288d1bb889ffec3"

        val response = BitcoinResponse(BlockHeightBitcoinResponse(5))

        mockPost(webClient, response)

        val result = client.getBlockHeight(hash)

        Assertions.assertThat(result).isEqualTo(5)
    }

    @Test
    fun getBlockHashShouldReturnExpectedValue() = runBlocking<Unit> {
        val hash = "0000000000015afb856ff92b062a4d023d104f7f1850914d9288d1bb889ffec3"

        val response = BitcoinResponse(hash)

        mockPost(webClient, response)

        val result = client.getBlockHash(5)

        Assertions.assertThat(result).isEqualTo(hash)
    }

    @Test
    fun getBlockShouldReturnExpectedValue() = runBlocking<Unit> {
        val hash = "0000000000015afb856ff92b062a4d023d104f7f1850914d9288d1bb889ffec3"

        val block = createDummyBitcoinBlock()

        val response = BitcoinResponse(block)

        mockPost(webClient, response)

        val result = client.getBlock(hash)

        Assertions.assertThat(result).isEqualTo(block)
    }

    @Test
    fun getInputAddressShouldReturnExpectedValue() = runBlocking<Unit> {
        val txId = "0000000000015afb856ff92b062a4d023d104f7f1850914d9288d1bb889ffec3"

        val info1 = InputInfo(0, "address1")

        val info2 = InputInfo(1, "address2")

        val details = TransactionInputBitcoinResponse(listOf(info1, info2))

        val response = BitcoinResponse(details)

        mockPost(webClient, response)

        val result = client.getInputAddress(txId, 0)

        Assertions.assertThat(result).isEqualTo("address1")
    }

}
