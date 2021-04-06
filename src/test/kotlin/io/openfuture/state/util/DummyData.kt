package io.openfuture.state.util

import io.openfuture.state.domain.*
import io.openfuture.state.webhhok.WebhookRestClient
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import java.math.BigDecimal
import java.time.LocalDateTime

fun createDummyWallet(
        blockchain: String = "Ethereum",
        address: String = "address",
        webhook: String = "webhook",
        webhookStatus: WebhookStatus = WebhookStatus.NOT_INVOKED,
        id: String = ObjectId().toHexString(),
        lastUpdate: LocalDateTime = LocalDateTime.of(2020, 10, 10, 10, 10)
) = Wallet(WalletIdentity(blockchain, address), webhook, webhookStatus, lastUpdate, id)

fun createDummyTransaction(
        blockchain: String = "Ethereum",
        address: String = "address",
        hash: String = "hash",
        from: String = "from",
        to: String = "to",
        amount: BigDecimal = BigDecimal(100),
        date: LocalDateTime = LocalDateTime.of(2020, 9, 9, 9, 9),
        blockHeight: Long = 1,
        blockHash: String = "block hash",
        id: String = ObjectId().toHexString()
) = Transaction(WalletIdentity(blockchain, address), hash, from, to, amount, date, blockHeight, blockHash, id)

fun createDummyTransactionQueueTask(
        transactionId: String = "transactionId",
        attempt: Int = 1,
        timestamp: LocalDateTime = LocalDateTime.of(2020, 9, 9, 9, 9),
) = TransactionQueueTask(transactionId, attempt, timestamp)

fun createDummyPositiveWebhookResponse(
        status: HttpStatus = HttpStatus.OK,
        url: String = "url",
        message: String? = null
) = WebhookRestClient.WebhookResponse(status, url, message)

fun createDummyNegativeWebhookResponse(
        status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
        url: String = "url",
        message: String? = null
) = WebhookRestClient.WebhookResponse(status, url, message)
