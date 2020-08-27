package io.openfuture.state.util

import io.openfuture.state.controller.domain.dto.WalletDto
import io.openfuture.state.controller.domain.request.SaveWalletRequest
import io.openfuture.state.model.Transaction
import io.openfuture.state.model.Wallet
import org.bson.types.ObjectId
import java.time.LocalDateTime
import java.util.*

fun createDummyWallet(
        id: ObjectId = ObjectId(),
        address: String = "address",
        webhook: String = "webhook",
        transactions: Set<Transaction> = setOf(createDummyTransaction()),
        lastUpdateDate: LocalDateTime = LocalDateTime.now()
) = Wallet(id, address, webhook, transactions, lastUpdateDate)

fun createDummyWalletDto(
        id: String = "id",
        address: String = "address",
        webhook: String = "webhook",
        lastUpdateDate: LocalDateTime = LocalDateTime.now()
) = WalletDto(id, address, webhook, lastUpdateDate)

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

fun createDummySaveWalletRequest(address: String = "address",
                                 webhook: String = "webhook") = SaveWalletRequest(address, webhook)
