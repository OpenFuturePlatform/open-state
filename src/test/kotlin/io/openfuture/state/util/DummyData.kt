package io.openfuture.state.util

import io.openfuture.state.controller.WalletController
import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import org.bson.types.ObjectId
import java.time.LocalDateTime
import java.util.*

fun createDummyWallet(
        id: ObjectId = ObjectId(),
        address: String = "address",
        webhook: String = "webhook",
        transactions: Set<Transaction> = setOf(createDummyTransaction()),
        lastUpdate: LocalDateTime = LocalDateTime.now()
) = Wallet(address, webhook, transactions, lastUpdate, id)

fun createDummyTransaction(
        hash: String = "hash",
        externalHash: String = "external hash",
        typeId: Int = 1,
        participant: String = "participant address",
        amount: Long = 100,
        fee: Long = 0,
        date: Long = Date().time,
        blockHeight: Long = 1,
        blockHash: String = "block hash"
) = Transaction(hash, externalHash, typeId, participant, amount, fee, date, blockHeight, blockHash)

fun createDummySaveWalletRequest(
        address: String = "address",
        webhook: String = "webhook"
) = WalletController.SaveWalletRequest(address, webhook)
