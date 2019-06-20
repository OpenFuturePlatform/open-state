package io.openfuture.state.entity

import io.openfuture.state.entity.base.Dictionary

enum class BlockhainType(private val id: Int) : Dictionary {
    OPEN(1),
    ETHEREUM(2),
    BINANCE(3);

    override fun getId(): Int = id

    companion object {

        fun getById(id: Int): BlockhainType {
            return values().find { id == it.getId() } ?: throw IllegalArgumentException("Invalid blockchain identifier")
        }

    }

}
