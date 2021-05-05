package io.openfuture.state.blockchain.bitcoin.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class BitcoinOutput(val value: BigDecimal, val n: Int) {

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
