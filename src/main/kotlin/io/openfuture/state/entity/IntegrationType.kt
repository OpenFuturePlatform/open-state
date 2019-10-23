package io.openfuture.state.entity

import io.openfuture.state.entity.base.Dictionary

enum class IntegrationType(private val id: Int, val title: String) : Dictionary {

    ETHEREUM(1, "Ethereum"),
    OPENCHAIN(2, "OPEN Chain");

    override fun getId(): Int = id

}