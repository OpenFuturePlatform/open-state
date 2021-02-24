package io.openfuture.state.util

import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import io.openfuture.state.domain.WebhookDeadQueue
import io.openfuture.state.domain.WebhookExecution
import io.openfuture.state.webhook.ScheduledTransaction
import io.openfuture.state.webhook.WebhookResponse
import io.openfuture.state.webhook.WebhookResult
import io.openfuture.state.webhook.WebhookStatus
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import java.math.BigDecimal
import java.time.LocalDateTime

fun createDummyWallet(
        blockchain: String = "EthereumBlockchain",
        address: String = "address",
        webhook: String = "webhook",
        status: WebhookStatus = WebhookStatus.NOT_INVOKED,
        id: ObjectId = ObjectId(),
        transactions: List<Transaction> = emptyList(),
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

fun createDummyWebhookExecution(
        walletAddress: String = "address",
        transactionHash: String = "hash",
        invocations: List<WebhookResult> = emptyList()
) = WebhookExecution(walletAddress, transactionHash, invocations)

fun createDummyScheduledTransaction(
        hash: String = "hash",
        attempts: Int = 1,
        timestamp: LocalDateTime = LocalDateTime.now()
) = ScheduledTransaction(hash, attempts, timestamp)

fun createDummyWebhookDeadQueue(
        walletAddress: String = "address",
        transactions: List<ScheduledTransaction> = emptyList(),
        timestamp: LocalDateTime = LocalDateTime.of(2020, 10, 10, 10, 10)
) = WebhookDeadQueue(walletAddress, transactions, timestamp)

fun createDummyPositiveWebhookResponse(
        status: HttpStatus = HttpStatus.OK,
        url: String = "webhook"
) = WebhookResponse(status, url)

fun createDummyNegativeWebhookResponse(
        status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
        url: String = "webhook",
        message: String = "Error"
) = WebhookResponse(status, url, message)
