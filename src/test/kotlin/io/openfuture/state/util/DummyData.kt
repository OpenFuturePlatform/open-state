package io.openfuture.state.util

import io.openfuture.state.controller.WalletController
import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import io.openfuture.state.model.BlockchainType
import org.bson.types.ObjectId
import java.time.LocalDateTime
import java.util.*

fun createDummyWallet(
        id: ObjectId = ObjectId(),
        address: String = "address",
        webhook: String = "webhook",
        transactions: MutableSet<Transaction> = mutableSetOf(createDummyTransaction()),
        lastUpdate: LocalDateTime = LocalDateTime.now()
) = Wallet(address, webhook, transactions, lastUpdate, id)

fun createDummyTransaction(
        blockchainType: BlockchainType = BlockchainType.ETHEREUM,
        hash: String = "hash",
        participant: String = "participant address",
        amount: Long = 100,
        fee: Long = 0,
        date: Long = Date().time,
        blockHeight: Long = 1,
        blockHash: String = "block hash"
) = Transaction(blockchainType, hash, participant, amount, fee, date, blockHeight, blockHash)

fun createDummySaveWalletRequest(
        address: String = "address",
        webhook: String = "webhook"
) = WalletController.SaveWalletRequest(address, webhook)
