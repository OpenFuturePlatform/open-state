package io.openfuture.state.controller.domain.dto

import io.openfuture.state.entity.OpenScaffold

data class OpenScaffoldDto(
        val id: Long,
        val recipientAddress: String,
        val webHook: String
) {

    constructor(openScaffold: OpenScaffold) : this(
            openScaffold.id,
            openScaffold.recipientAddress,
            openScaffold.webHook
    )

}
