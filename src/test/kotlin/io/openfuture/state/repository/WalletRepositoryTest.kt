package io.openfuture.state.repository

import io.openfuture.state.base.MongoRepositoryTests
import io.openfuture.state.domain.WalletIdentity
import io.openfuture.state.util.createDummyWallet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class WalletRepositoryTest : MongoRepositoryTests() {

    @Autowired
    private lateinit var walletRepository: WalletRepository

    @AfterEach
    fun tearDown() {
        walletRepository.deleteAll().block()
    }

    @Test
    fun findByIdentityShouldReturnWallet() {
        var wallet = createDummyWallet(blockchain = "Ethereum", address = "address", id = "walletId")
        wallet = walletRepository.save(wallet).block()!!

        val result = walletRepository.findByIdentity(WalletIdentity(wallet.identity.blockchain, wallet.identity.address)).block()!!
        assertThat(result).isEqualTo(wallet)
    }
}
