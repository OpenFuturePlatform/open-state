package io.openfuture.state.util

import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import org.bson.types.ObjectId
import java.time.LocalDateTime

fun createDummyWallet(
        blockchain: String = "EthereumBlockchain",
        id: ObjectId = ObjectId(),
        address: String = "address",
        webhook: String = "webhook",
        lastUpdate: LocalDateTime = LocalDateTime.now()
) = Wallet(blockchain, address, webhook, lastUpdate, id)

fun createDummyTransaction(
        hash: String = "hash",
        participant: String = "participant address",
        amount: Long = 100,
        fee: Long = 0,
        date: LocalDateTime = LocalDateTime.now(),
        blockHeight: Long = 1,
        blockHash: String = "block hash"
) = Transaction(hash, participant, amount, fee, date, blockHeight, blockHash)
