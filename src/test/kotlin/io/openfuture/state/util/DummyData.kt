package io.openfuture.state.util

import io.openfuture.state.controller.domain.dto.TransactionDto
import io.openfuture.state.entity.*
import java.util.*

fun createDummyAccount(
        webHook: String = "http://test-webhook.com",
        isEnabled: Boolean = true,
        wallets: MutableSet<Wallet> = mutableSetOf(createDummyWallet())
) = Account(webHook, isEnabled, wallets)

fun createDummyWallet(
        accounts: MutableSet<Account> = mutableSetOf(),
        blockchain: Blockchain = createDummyBlockchain(),
        address: String = "address",
        state: State = createDummyState(),
        startTrackingDate: Long = Date().time,
        isActive: Boolean = true
) = Wallet(accounts, blockchain, address, state, startTrackingDate, isActive)

fun createDummyState(
        balance: Long = 100,
        root: String = "test root hash",
        date: Long = Date().time
) = State(balance, root, date)

fun createDummyBlockchain(
        coin: Coin = createDummyCoin(),
        title: String = "Test Blockchain"
) = Blockchain(coin, title)

fun createDummyCoin(
        title: String = "Test Coin",
        shortTitle: String = "TC",
        decimals: Int = 1
) = Coin(title, shortTitle, decimals)

fun createDummyTransaction(
        wallet: Wallet = createDummyWallet(),
        hash: String = "hash",
        externalHash: String = "external hash",
        typeId: Int = 1,
        participant: String = "participant address",
        amount: Long = 100,
        fee: Long = 0,
        date: Long = Date().time,
        blockHeight: Long = 1,
        blockHash: String = "block hash"
) = Transaction(wallet, hash, externalHash, typeId, participant, amount, fee, date, blockHeight, blockHash)

fun createDummyTransactionDto(
        blockchainId: Long = 1,
        hash: String = "external hash",
        from: String = "address1",
        to: String = "address2",
        amount: Long = 100,
        fee: Long = 0,
        date: Long = Date().time,
        blockHeight: Long = 1,
        blockHash: String = "block hash"
) = TransactionDto(blockchainId, hash, from, to, amount, fee, date, blockHeight, blockHash)
