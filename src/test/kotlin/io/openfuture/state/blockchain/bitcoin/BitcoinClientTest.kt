package io.openfuture.state.blockchain.bitcoin

import io.openfuture.state.property.BitcoinProperties
import io.openfuture.state.util.createDummyBitcoinBlock
import io.openfuture.state.util.readResource
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.web.reactive.function.client.WebClient


@RestClientTest
@TestInstance(PER_CLASS)
class BitcoinClientTest {

    private lateinit var client: BitcoinClient
    private lateinit var mockWebServer: MockWebServer

    @BeforeAll
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @AfterAll
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @BeforeEach
    fun init() {
        val properties = BitcoinProperties("http://${mockWebServer.hostName}:${mockWebServer.port}", "test", "test")
        client = BitcoinClient(properties, WebClient.builder())
    }

    @Test
    fun getLatestBlockHashShouldReturnExpectedValue() = runBlocking<Unit> {
        val json = readResource<BitcoinClientTest>("latest_block_hash_response.json")
        val mockResponse = MockResponse().setBody(json).addHeader("Content-Type", "application/json")
        mockWebServer.enqueue(mockResponse)

        val result = client.getLatestBlockHash()

        assertThat(result).isEqualTo("0000000000015afb856ff92b062a4d023d104f7f1850914d9288d1bb889ffec3")
    }

    @Test
    fun getBlockHeightShouldReturnExpectedValue() = runBlocking<Unit> {
        val json = readResource<BitcoinClientTest>("block_height_response.json")
        val mockResponse = MockResponse().setBody(json).addHeader("Content-Type", "application/json")
        mockWebServer.enqueue(mockResponse)

        val result = client.getBlockHeight("hash")

        assertThat(result).isEqualTo(666)
    }

    @Test
    fun getBlockHashShouldReturnExpectedValue() = runBlocking<Unit> {
        val json = readResource<BitcoinClientTest>("block_hash_response.json")
        val mockResponse = MockResponse().setBody(json).addHeader("Content-Type", "application/json")
        mockWebServer.enqueue(mockResponse)

        val result = client.getBlockHash(5)

        assertThat(result).isEqualTo("0000000000015afb856ff92b062a4d023d104f7f1850914d9288d1bb889ffec3")
    }

    @Test
    fun getBlockShouldReturnExpectedValue() = runBlocking<Unit> {
        val expected = createDummyBitcoinBlock()
        val json = readResource<BitcoinClientTest>("block_response.json")
        val mockResponse = MockResponse().setBody(json).addHeader("Content-Type", "application/json")
        mockWebServer.enqueue(mockResponse)

        val result = client.getBlock("hash")

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun getInputAddressShouldReturnExpectedValue() = runBlocking<Unit> {
        val json = readResource<BitcoinClientTest>("input_address_response.json")
        val mockResponse = MockResponse().setBody(json).addHeader("Content-Type", "application/json")
        mockWebServer.enqueue(mockResponse)

        val result = client.getInputAddress("hash", 0)

        assertThat(result).isEqualTo("address1")
    }

}
