package io.openfuture.state.blockchain.bitcoin.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class BitcoinTransaction(
    val hash: String,

    @field:JsonProperty("vin")
    val inputs: List<Input>,

    @field:JsonProperty("vout")
    val outputs: List<BitcoinOutput>
) {

    data class Input(
        @field:JsonProperty("txid")
        val txId: String?,

        @field:JsonProperty("vout")
        val outputNumber: Int?
    )

}
