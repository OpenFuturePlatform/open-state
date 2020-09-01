package io.openfuture.state.util

import io.openfuture.state.controller.WalletController
import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import io.openfuture.state.model.Blockchain
import org.bson.types.ObjectId
import java.time.LocalDateTime
import java.util.*

fun createDummyWallet(
        id: ObjectId = ObjectId(),
        address: String = "address",
        webhook: String = "webhook",
        blockchain: Blockchain = Blockchain.ETHEREUM,
        lastUpdate: LocalDateTime = LocalDateTime.now()
) = Wallet(address, webhook, blockchain, lastUpdate, id)

fun createDummyTransaction(
        hash: String = "hash",
        participant: String = "participant address",
        amount: Long = 100,
        fee: Long = 0,
        date: Long = Date().time,
        blockHeight: Long = 1,
        blockHash: String = "block hash"
) = Transaction(hash, participant, amount, fee, date, blockHeight, blockHash)

fun createDummySaveWalletRequest(
        address: String = "address",
        webhook: String = "webhook",
        blockchain: Blockchain = Blockchain.ETHEREUM
) = WalletController.SaveWalletRequest(address, webhook, blockchain)
