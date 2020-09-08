package io.openfuture.state.util

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.controller.WalletController
import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import org.bson.types.ObjectId
import java.time.LocalDateTime

fun createDummyWallet(
        id: ObjectId = ObjectId(),
        address: String = "address",
        webhook: String = "webhook",
        blockchain: Blockchain = createDummyBlockchain(),
        lastUpdate: LocalDateTime = LocalDateTime.now()
) = Wallet(address, webhook, blockchain.getName(), lastUpdate, id)

fun createDummyTransaction(
        hash: String = "hash",
        participant: String = "participant address",
        amount: Long = 100,
        fee: Long = 0,
        date: LocalDateTime = LocalDateTime.now(),
        blockHeight: Long = 1,
        blockHash: String = "block hash"
) = Transaction(hash, participant, amount, fee, date, blockHeight, blockHash)

fun createDummySaveWalletRequest(
        address: String = "address",
        webhook: String = "webhook",
        blockchain: Blockchain = createDummyBlockchain()
) = WalletController.SaveWalletRequest(address, webhook, blockchain.getName())

fun createDummyBlockchain() = MockBlockchain()

class MockBlockchain : Blockchain() {
    override suspend fun getLastBlockNumber(): Long {
        return 0
    }

    override suspend fun getBlock(blockNumber: Long): UnifiedBlock {
        return UnifiedBlock(emptyList(), LocalDateTime.now())
    }
}
