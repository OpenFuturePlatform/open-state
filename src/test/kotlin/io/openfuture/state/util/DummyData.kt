package io.openfuture.state.util

import io.openfuture.state.entity.*
import java.util.*

fun createDummyAccount(
        webHook: String = "http://webhook.com",
        isEnabled: Boolean = true,
        wallets: MutableSet<Wallet> = mutableSetOf(createDummyWallet())
) = Account(webHook, isEnabled, wallets)

fun createDummyWallet(
        accounts: MutableSet<Account> = mutableSetOf(),
        blockchain: Blockchain = createDummyBlockchain(),
        address: String = "address",
        state: State = createDummyState(),
        startTrackingDate: Long = Date().time
) = Wallet(accounts, blockchain, address, state, startTrackingDate)

fun createDummyState(
        balance: Double = 100.0,
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
