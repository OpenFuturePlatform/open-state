package io.openfuture.state.util

import io.openfuture.state.blockchain.bitcoin.dto.BitcoinBlock
import io.openfuture.state.blockchain.bitcoin.dto.BitcoinOutput
import io.openfuture.state.blockchain.bitcoin.dto.BitcoinTransaction
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.blockchain.dto.UnifiedTransaction
import io.openfuture.state.domain.*
import io.openfuture.state.webhook.WebhookRestClient
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

fun createDummyWallet(
    blockchain: String = "Ethereum",
    address: String = "address",
    rate: BigDecimal = BigDecimal.ONE
) = Wallet(WalletIdentity(blockchain, address), rate = rate,)

fun createDummyTransaction(
    blockchain: String = "Ethereum",
    address: String = "address",
    hash: String = "hash",
    from: Set<String> = setOf("from"),
    to: String = "to",
    amount: BigDecimal = BigDecimal(100),
    date: LocalDateTime = LocalDateTime.of(2020, 9, 9, 9, 9),
    blockHeight: Long = 1,
    blockHash: String = "block hash",
    id: String = ObjectId().toHexString()
) = Transaction(WalletIdentity(blockchain, address), hash, from, to, amount, date, blockHeight, blockHash, id)

fun createDummyTransactionQueueTask(
    transactionId: String = "transactionId",
    timestamp: LocalDateTime = LocalDateTime.of(2020, 9, 9, 9, 9),
    attempt: Int = 1
) = TransactionQueueTask(transactionId, timestamp, attempt)

fun createDummyWalletQueueTask(
    walletId: String = "walletId",
    score: Double = 5.0
) = WalletQueueTask(walletId, score)

fun createDummyUnifiedBlock(
    transactions: List<UnifiedTransaction> = listOf(createDummyUnifiedTransaction()),
    number: Long = 1,
    hash: String = "hash",
    date: LocalDateTime = 1616862860L.toLocalDateTimeInSeconds(),
) = UnifiedBlock(transactions, date, number, hash)

fun createDummyUnifiedTransaction(
    hash: String = "hash",
    from: Set<String> = setOf("from"),
    to: String = "to",
    amount: BigDecimal = BigDecimal.ONE
) = UnifiedTransaction(hash, from, to, amount)

fun createDummyBitcoinBlock(
    hash: String = "hash",
    height: Long = 1,
    time: Long = 1616862860,
    transactions: List<BitcoinTransaction> = listOf(createDummyBitcoinTransaction(), createDummyBitcoinTransaction())
) = BitcoinBlock(hash, height, time, transactions)

fun createDummyBitcoinTransaction(
    hash: String = "hash",
    input: List<BitcoinTransaction.Input> = listOf(BitcoinTransaction.Input("id", 1)),
    output: List<BitcoinOutput> = listOf(createDummyBitcoinOutput())
) = BitcoinTransaction(hash, input, output)

fun createDummyBitcoinOutput(
    value: BigDecimal = BigDecimal.ONE,
    address: String = "to",
    n: Int = 0
): BitcoinOutput {
    val output = BitcoinOutput(value, n)
    output.addresses.add(address)
    return output
}

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

fun createDummyTransactionDeadQueue(
    identity: WalletIdentity = WalletIdentity("Ethereum", "address"),
    transactions: MutableList<TransactionQueueTask> = LinkedList(),
    timestamp: LocalDateTime = LocalDateTime.now(),
    id: String = ObjectId().toHexString()
) = TransactionDeadQueue(identity, transactions, timestamp, id)
