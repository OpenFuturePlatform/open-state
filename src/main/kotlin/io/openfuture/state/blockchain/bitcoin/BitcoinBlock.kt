package io.openfuture.state.blockchain.bitcoin

import com.fasterxml.jackson.annotation.JsonProperty

data class BitcoinBlock(
        val hash: String,
        val height: Long,
        val time: Long,
        @field:JsonProperty("tx") val transactions: List<BitcoinTransaction>
)
