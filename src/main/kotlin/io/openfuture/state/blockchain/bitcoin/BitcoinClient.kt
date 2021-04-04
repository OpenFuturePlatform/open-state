package io.openfuture.state.blockchain.bitcoin

import io.openfuture.state.blockchain.bitcoin.dto.*
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class BitcoinClient(private val client: WebClient) {

    suspend fun getLatestBlockHash(): String {
        val command = BitcoinCommand("getbestblockhash")
        return client
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(command))
                .retrieve()
                .awaitBody<BitcoinResponse<String>>()
                .result
    }

    suspend fun getBlockHeight(blockHash: String): Int {
        val command = BitcoinCommand("getblock", listOf(blockHash, 1))
        return client
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(command))
                .retrieve()
                .awaitBody<BitcoinResponse<BlockHeightBitcoinResponse>>()
                .result
                .height
    }

    suspend fun getBlockHash(blockHeight: Int): String {
        val command = BitcoinCommand("getblockhash", listOf(blockHeight))
        return client
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(command))
                .retrieve()
                .awaitBody<BitcoinResponse<String>>()
                .result
    }

    suspend fun getBlock(blockHash: String): BitcoinBlock {
        val command = BitcoinCommand("getblock", listOf(blockHash, 2))
        return client
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(command))
                .retrieve()
                .awaitBody<BitcoinResponse<BitcoinBlock>>()
                .result
    }

    suspend fun getInputAddress(txId: String, outputNumber: Int): String {
        val command = BitcoinCommand("gettransaction", listOf(txId, true))
        return client
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(command))
                .retrieve()
                .awaitBody<BitcoinResponse<TransactionInputBitcoinResponse>>()
                .result
                .details
                .first { it.vout == outputNumber }
                .address
    }

}
