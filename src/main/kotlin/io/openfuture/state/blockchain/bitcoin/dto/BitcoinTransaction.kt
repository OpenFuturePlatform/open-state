package io.openfuture.state.blockchain.bitcoin.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class BitcoinTransaction(
        val hash: String,

        @field:JsonProperty("vin")
        val inputs: List<Input>,

        @field:JsonProperty("vout")
        val outputs: List<Output>
) {

    data class Input(
            @field:JsonProperty("txid")
            val txId: String?,

            @field:JsonProperty("vout")
            val outputNumber: Int?
    )

    data class Output(val value: BigDecimal) {

        val addresses: MutableSet<String> = mutableSetOf()

        @Suppress("UNCHECKED_CAST")
        @JsonProperty("scriptPubKey")
        private fun unpackNested(scriptPubKey: Map<String, Any>) {
            val outputAddresses = scriptPubKey["addresses"]
            outputAddresses?.let {
                this.addresses.addAll((it as List<String>))
            }
        }
    }

}
