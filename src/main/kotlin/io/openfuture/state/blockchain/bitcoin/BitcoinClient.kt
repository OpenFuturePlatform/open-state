package io.openfuture.state.blockchain.bitcoin

import io.openfuture.state.blockchain.bitcoin.dto.*
import io.openfuture.state.property.BitcoinProperties
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class BitcoinClient(
    private val properties: BitcoinProperties,
    webClientBuilder: WebClient.Builder
) {

    private val client: WebClient = webClientBuilder
        .codecs { it.defaultCodecs().maxInMemorySize(16 * 1024 * 1024) }
        .baseUrl(properties.nodeAddress!!)
        .defaultHeaders { it.setBasicAuth(properties.username!!, properties.password!!) }
        .build()

    suspend fun getLatestBlockHash(): String {
        val command = BitcoinCommand("getbestblockhash")
        val response = client.post()
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(command))
            .retrieve()
            .awaitBody<BitcoinResponse<String>>()
        return response.result
    }

    suspend fun getBlockHeight(blockHash: String): Int {
        val command = BitcoinCommand("getblock", listOf(blockHash, 1))
        val response = client.post()
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(command))
            .retrieve()
            .awaitBody<BitcoinResponse<BlockHeightBitcoinResponse>>()
        return response.result.height
    }

    suspend fun getBlockHash(blockHeight: Int): String {
        val command = BitcoinCommand("getblockhash", listOf(blockHeight))
        val response = client.post()
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(command))
            .retrieve()
            .awaitBody<BitcoinResponse<String>>()
        return response.result
    }

    suspend fun getBlock(blockHash: String): BitcoinBlock {
        val command = BitcoinCommand("getblock", listOf(blockHash, 2))
        val response = client.post()
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(command))
            .retrieve()
            .awaitBody<BitcoinResponse<BitcoinBlock>>()
        return response.result
    }

    suspend fun getInputAddress(txId: String, outputNumber: Int): String {
        val command = BitcoinCommand("gettransaction", listOf(txId, true))
        val response = client.post()
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(command))
            .retrieve()
            .awaitBody<BitcoinResponse<TransactionInputBitcoinResponse>>()
        return response.result.details
            .first { it.vout == outputNumber }
            .address
    }

}
