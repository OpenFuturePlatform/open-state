package io.openfuture.state.blockchain.bitcoin

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class BitcoinRpcClient(private val bitcoinWebClient: WebClient) {

    suspend fun getLatestBlockHash(): String {
        val command = Command("getbestblockhash", emptyList())
        return bitcoinWebClient
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(command))
                .retrieve()
                .awaitBody<Response<String>>()
                .result
    }

    suspend fun getBlockHeight(blockHash: String): Int {
        val command = Command("getblock", listOf(blockHash, 1))
        return bitcoinWebClient
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(command))
                .retrieve()
                .awaitBody<Response<BlockHeightResponse>>()
                .result
                .height
    }

    suspend fun getBlockHash(blockHeight: Int): String {
        val command = Command("getblockhash", listOf(blockHeight))
        return bitcoinWebClient
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(command))
                .retrieve()
                .awaitBody<Response<String>>()
                .result
    }

    suspend fun getBlock(blockHash: String): BitcoinBlock {
        val command = Command("getblock", listOf(blockHash, 2))
        return bitcoinWebClient
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(command))
                .retrieve()
                .awaitBody<Response<BitcoinBlock>>()
                .result
    }

    suspend fun getInputAddress(txId: String, outputNumber: Int): String {
        val command = Command("gettransaction", listOf(txId, true))
        return bitcoinWebClient
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(command))
                .retrieve()
                .awaitBody<Response<TransactionInputResponse>>()
                .result
                .details
                .first { it.vout == outputNumber }
                .address
    }

    private data class Command(val method: String, val params: List<Any> = emptyList())
    data class Response<T>(val result: T)
    data class BlockHeightResponse(val height: Int)
    data class TransactionInputResponse(val details: List<InputInfo>)
    data class InputInfo(val vout: Int, val address: String)

}
