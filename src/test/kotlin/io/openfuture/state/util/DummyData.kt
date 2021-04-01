package io.openfuture.state.util

import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WalletIdentity
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.LocalDateTime

fun createDummyWallet(
        blockchain: String = "Ethereum",
        address: String = "address",
        webhook: String = "webhook",
        id: String = ObjectId().toHexString(),
        lastUpdate: LocalDateTime = LocalDateTime.of(2020, 10, 10, 10, 10)
) = Wallet(WalletIdentity(blockchain, address), webhook, lastUpdate, id)

fun createDummyTransaction(
        blockchain: String = "Ethereum",
        address: String = "address",
        hash: String = "hash",
        from: String = "from",
        to: String = "to",
        amount: BigDecimal = BigDecimal(100),
        date: LocalDateTime = LocalDateTime.of(2020, 9, 9, 9, 9),
        blockHeight: Long = 1,
        blockHash: String = "block hash"
) = Transaction(WalletIdentity(blockchain, address), hash, from, to, amount, date, blockHeight, blockHash)
