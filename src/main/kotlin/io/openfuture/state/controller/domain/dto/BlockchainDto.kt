package io.openfuture.state.controller.domain.dto

import io.openfuture.state.entity.Blockchain

data class BlockchainDto(
        val id: Long,
        val title: String,
        val coinName: String,
        val coinShortName: String,
        val coinDecimals: Int
) {

    constructor(blockchain: Blockchain) : this(
            blockchain.id,
            blockchain.title,
            blockchain.coin.title,
            blockchain.coin.shortTitle,
            blockchain.coin.decimals
    )

}
