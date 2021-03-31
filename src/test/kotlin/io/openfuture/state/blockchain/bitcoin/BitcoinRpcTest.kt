package io.openfuture.state.blockchain.bitcoin

import com.nhaarman.mockitokotlin2.mock
import io.openfuture.state.util.createDummyBitcoinBlock
import io.openfuture.state.util.mockPost
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient

class BitcoinRpcTest {

    private val webClient: WebClient = mock()
    private lateinit var rpcClient: BitcoinRpcClient

    @BeforeEach
    fun setUp() {
        rpcClient = BitcoinRpcClient(webClient)
    }

    @Test
    fun getLatestBlockHashShouldReturnExpectedValue() = runBlocking<Unit> {
        val hash = "0000000000015afb856ff92b062a4d023d104f7f1850914d9288d1bb889ffec3"

        val response = BitcoinRpcClient.Response(hash)

        mockPost(webClient, response)

        val result = rpcClient.getLatestBlockHash()

        Assertions.assertThat(result).isEqualTo(hash)
    }

    @Test
    fun getBlockHeightShouldReturnExpectedValue() = runBlocking<Unit> {
        val hash = "0000000000015afb856ff92b062a4d023d104f7f1850914d9288d1bb889ffec3"

        val response = BitcoinRpcClient.Response(
                BitcoinRpcClient.BlockHeightResponse(5)
        )

        mockPost(webClient, response)

        val result = rpcClient.getBlockHeight(hash)

        Assertions.assertThat(result).isEqualTo(5)
    }

    @Test
    fun getBlockHashShouldReturnExpectedValue() = runBlocking<Unit> {
        val hash = "0000000000015afb856ff92b062a4d023d104f7f1850914d9288d1bb889ffec3"

        val response = BitcoinRpcClient.Response(hash)

        mockPost(webClient, response)

        val result = rpcClient.getBlockHash(5)

        Assertions.assertThat(result).isEqualTo(hash)
    }

    @Test
    fun getBlockShouldReturnExpectedValue() = runBlocking<Unit> {
        val hash = "0000000000015afb856ff92b062a4d023d104f7f1850914d9288d1bb889ffec3"

        val block = createDummyBitcoinBlock()

        val response = BitcoinRpcClient.Response(block)

        mockPost(webClient, response)

        val result = rpcClient.getBlock(hash)

        Assertions.assertThat(result).isEqualTo(block)
    }

    @Test
    fun getInputAddressShouldReturnExpectedValue() = runBlocking<Unit> {
        val txId = "0000000000015afb856ff92b062a4d023d104f7f1850914d9288d1bb889ffec3"

        val info1 = BitcoinRpcClient.InputInfo(
                vout = 0,
                "address1"
        )

        val info2 = BitcoinRpcClient.InputInfo(
                vout = 1,
                "address2"
        )

        val details = BitcoinRpcClient.TransactionInputResponse(
                details = listOf(info1, info2)
        )

        val response = BitcoinRpcClient.Response(details)

        mockPost(webClient, response)

        val result = rpcClient.getInputAddress(txId, 0)

        Assertions.assertThat(result).isEqualTo("address1")
    }

}
