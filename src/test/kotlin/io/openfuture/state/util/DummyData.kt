package io.openfuture.state.util

import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.LocalDateTime

fun createDummyWallet(
        blockchain: String = "EthereumBlockchain",
        address: String = "address",
        webhook: String = "webhook",
        id: ObjectId = ObjectId(),
        transactions: List<Transaction> = arrayListOf(createDummyTransaction()),
        lastUpdate: LocalDateTime = LocalDateTime.of(2020, 10, 10, 10, 10)
) = Wallet(blockchain, address, webhook, transactions, lastUpdate, id)

fun createDummyTransaction(
        hash: String = "hash",
        from: String = "from",
        to: String = "to",
        amount: BigDecimal = BigDecimal(100),
        date: LocalDateTime = LocalDateTime.of(2020, 9, 9, 9, 9),
        blockHeight: Long = 1,
        blockHash: String = "block hash"
) = Transaction(hash, from, to, amount, date, blockHeight, blockHash)
