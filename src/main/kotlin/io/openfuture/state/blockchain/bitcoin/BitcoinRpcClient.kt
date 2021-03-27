package io.openfuture.state.blockchain.bitcoin

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

@Component
class BitcoinRpcClient(private val bitcoinWebClient: WebClient, private val objectMapper: ObjectMapper) {

    suspend fun getLatestBlockHash(): String {
        val command = Command(GET_LATEST_BLOCK_COMMAND, emptyList())
        return bitcoinWebClient
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(command))
                .retrieve()
                .bodyToMono(String()::class.java)
                .map { objectMapper.readTree(it).get("result").asText() }
                .awaitSingle()
    }

    suspend fun getBlockHeight(blockHash: String): Int {
        val command = Command(GET_BLOCK_COMMAND, listOf(blockHash, 1))
        return bitcoinWebClient
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(command))
                .retrieve()
                .bodyToMono(String::class.java)
                .map { objectMapper.readTree(it).get("result").get("height").asInt() }
                .awaitSingle()
    }

    suspend fun getBlockHash(blockHeight: Int): String {
        val command = Command(GET_BLOCK_HASH_COMMAND, listOf(blockHeight))
        return bitcoinWebClient
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(command))
                .retrieve()
                .bodyToMono(String::class.java)
                .map { objectMapper.readTree(it).get("result").asText() }
                .awaitSingle()
    }

    suspend fun getBlock(blockHash: String): BitcoinBlock {
        val command = Command(GET_BLOCK_COMMAND, listOf(blockHash, 2))
        return bitcoinWebClient
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(command))
                .retrieve()
                .bodyToMono(String::class.java)
                .map {
                    val node = objectMapper.readTree(it).get("result")
                    objectMapper.treeToValue(node, BitcoinBlock::class.java)
                }
                .awaitSingle()
    }

    suspend fun getInputAddress(txId: String, outputNumber: Int): String {
        val command = Command(GET_TRANSACTION_COMMAND, listOf(txId, true))
        return bitcoinWebClient
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(command))
                .retrieve()
                .bodyToMono(String::class.java)
                .map { body ->
                    objectMapper.readTree(body)
                            .get("result")
                            .get("details")
                            .asIterable()
                            .filter { it.get("vout").asInt() == outputNumber }
                            .map { it.get("address").asText() }.first()
                }
                .awaitSingle()
    }

    private data class Command(val method: String, val params: List<Any>)

    private companion object {
        private const val GET_LATEST_BLOCK_COMMAND = "getbestblockhash"
        private const val GET_BLOCK_COMMAND = "getblock"
        private const val GET_BLOCK_HASH_COMMAND = "getblockhash"
        private const val GET_TRANSACTION_COMMAND = "gettransaction"
    }

}
