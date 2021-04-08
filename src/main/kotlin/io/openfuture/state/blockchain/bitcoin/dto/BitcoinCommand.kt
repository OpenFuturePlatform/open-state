package io.openfuture.state.blockchain.bitcoin.dto

data class BitcoinCommand(val method: String, val params: List<Any> = emptyList())
