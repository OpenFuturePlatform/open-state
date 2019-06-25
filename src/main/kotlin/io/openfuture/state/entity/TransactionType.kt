package io.openfuture.state.entity

import io.openfuture.state.entity.base.Dictionary

enum class TransactionType(private val id: Int) : Dictionary {

    INPUT(1),
    OUTPUT(2);

    override fun getId(): Int = id

}
