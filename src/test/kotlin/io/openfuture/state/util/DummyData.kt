package io.openfuture.state.util

import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WebhookInvocation
import io.openfuture.state.webhook.WebhookStatus
import org.bson.types.ObjectId
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.MongoId
import java.math.BigDecimal
import java.time.LocalDateTime

fun createDummyWallet(
        blockchain: String = "EthereumBlockchain",
        address: String = "address",
        webhook: String = "webhook",
        status: WebhookStatus = WebhookStatus.NOT_INVOKED,
        id: ObjectId = ObjectId(),
        transactions: List<Transaction> = arrayListOf(createDummyTransaction()),
        lastUpdate: LocalDateTime = LocalDateTime.of(2020, 10, 10, 10, 10)
) = Wallet(blockchain, address, webhook, status, transactions, lastUpdate, id)

fun createDummyTransaction(
        hash: String = "hash",
        from: String = "from",
        to: String = "to",
        amount: BigDecimal = BigDecimal(100),
        date: LocalDateTime = LocalDateTime.of(2020, 9, 9, 9, 9),
        blockHeight: Long = 1,
        blockHash: String = "block hash"
) = Transaction(hash, from, to, amount, date, blockHeight, blockHash)

fun createDummyWebhookInvocation(
        wallet: Wallet = createDummyWallet(),
        transaction: Transaction = createDummyTransaction(),
        url: String = "google.com",
        attempts: Int = 0,
        message: String? = null,
        id: ObjectId = ObjectId(),
        lastUpdate: LocalDateTime = LocalDateTime.of(2020, 10, 10, 10, 10),
        status: WebhookStatus = WebhookStatus.NOT_INVOKED
) = WebhookInvocation(wallet, transaction, url, attempts, message, id, lastUpdate, status)
