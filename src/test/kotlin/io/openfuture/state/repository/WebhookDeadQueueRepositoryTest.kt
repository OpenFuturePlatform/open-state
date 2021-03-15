package io.openfuture.state.repository

import io.openfuture.state.base.MongoRepositoryTests
import io.openfuture.state.util.createDummyWebhookDeadQueue
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class WebhookDeadQueueRepositoryTest: MongoRepositoryTests() {

    @Autowired
    private lateinit var deadQueueRepository: WebhookDeadQueueRepository


    @AfterEach
    fun tearDown() {
        deadQueueRepository.deleteAll().block()
    }

    @Test
    fun findByWalletKeyShouldReturnWalletDeadQueue() {
        var deadQueue = createDummyWebhookDeadQueue()
        deadQueue = deadQueueRepository.save(deadQueue).block()!!

        val result = deadQueueRepository.findByWalletKey("walletKey").block()!!
        Assertions.assertThat(result).isEqualTo(deadQueue)
    }

    @Test
    fun findByWalletKeyShouldReturnNull() {
        val result = deadQueueRepository.findByWalletKey("walletKey").block()
        Assertions.assertThat(result).isNull()
    }

    @Test
    fun existsByWalletKeyShouldReturnFalse() {
        val result = deadQueueRepository.existsByWalletKey("walletKey").block()
        Assertions.assertThat(result).isEqualTo(false)
    }

    @Test
    fun existsByWalletKeyShouldReturnTrue() {
        val deadQueue = createDummyWebhookDeadQueue()
        deadQueueRepository.save(deadQueue).block()!!

        val result = deadQueueRepository.existsByWalletKey("walletKey").block()!!
        Assertions.assertThat(result).isEqualTo(true)
    }

    @Test
    fun deleteByWalletKeyShouldRemoveValue() {
        val deadQueue = createDummyWebhookDeadQueue()
        deadQueueRepository.save(deadQueue).block()!!

        deadQueueRepository.deleteByWalletKey("walletKey").block()

        val result = deadQueueRepository.existsByWalletKey("walletKey").block()!!
        Assertions.assertThat(result).isEqualTo(false)
    }
}
