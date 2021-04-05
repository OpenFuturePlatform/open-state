package io.openfuture.state.util

import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.blockchain.dto.UnifiedTransaction
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

fun createDummyUnifiedBlock(
        transactions: List<UnifiedTransaction> = listOf(createDummyUnifiedTransaction()),
        number: Long = 1,
        hash: String = "hash",
        date: LocalDateTime = LocalDateTime.of(2020, 9, 9, 9, 9),
) = UnifiedBlock(transactions, date, number, hash)

fun createDummyUnifiedTransaction(
        hash: String = "hash",
        from: String = "from",
        to: String = "to",
        amount: BigDecimal = BigDecimal.ONE
) = UnifiedTransaction(hash, from, to, amount)
