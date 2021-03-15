package io.openfuture.state.repository

import io.openfuture.state.base.MongoRepositoryTests
import io.openfuture.state.util.createDummyTransaction
import io.openfuture.state.util.createDummyWallet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class WalletRepositoryTest : MongoRepositoryTests() {

    @Autowired
    private lateinit var walletRepository: WalletRepository

    @Autowired
    private lateinit var transactionRepository: TransactionRepository


    @AfterEach
    fun tearDown() {
        transactionRepository.deleteAll().block()
        walletRepository.deleteAll().block()
    }

    @Test
    fun findByBlockchainAndAddressShouldReturnWallet() {
        var wallet = createDummyWallet(blockchain = "Ethereum")
        wallet = walletRepository.save(wallet).block()!!

        val result = walletRepository.findByBlockchainAndAddress("Ethereum", "address").block()!!
        assertThat(result).isEqualTo(wallet)
    }

    @Test
    fun findByBlockchainAndAddressShouldReturnNull() {
        val result = walletRepository.findByBlockchainAndAddress("Ethereum", "address").block()
        assertThat(result).isNull()
    }

    @Test
    fun saveShouldSaveWalletWithTransactions() {
        val wallet = createDummyWallet()
        var transaction = createDummyTransaction()

        transaction = transactionRepository.save(transaction).block()!!
        wallet.addTransaction(transaction)

        val result = walletRepository.save(wallet).block()!!
        assertThat(result).isEqualTo(wallet)
    }
}
