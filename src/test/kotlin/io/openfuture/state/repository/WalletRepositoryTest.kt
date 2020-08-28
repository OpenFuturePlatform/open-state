package io.openfuture.state.repository

import io.openfuture.state.base.MongoRepositoryTests
import io.openfuture.state.util.createDummyWallet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

class WalletRepositoryTest : MongoRepositoryTests() {

    @Autowired
    private lateinit var walletRepository: WalletRepository

    @AfterEach
    fun tearDown() {
        walletRepository.deleteAll().block()
    }

    @Test
    fun findByAddressShouldReturnWallet() {
        val now = LocalDateTime.now()
        var wallet = createDummyWallet(address = "address", lastUpdateDate = now)

        wallet = walletRepository.save(wallet).block()!!

        val result = walletRepository.findByAddress("address").block()!!
        result.lastUpdateDate = now
        assertThat(result).isEqualTo(wallet)
    }
}
