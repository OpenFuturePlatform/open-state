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
    fun findByWalletAddressShouldReturnWalletDeadQueue() {
        var deadQueue = createDummyWebhookDeadQueue()
        deadQueue = deadQueueRepository.save(deadQueue).block()!!

        val result = deadQueueRepository.findByWalletAddress("address").block()!!
        Assertions.assertThat(result).isEqualTo(deadQueue)
    }

    @Test
    fun findByWalletAddressShouldReturnNull() {
        val result = deadQueueRepository.findByWalletAddress("address").block()
        Assertions.assertThat(result).isNull()
    }

    @Test
    fun existsByWalletAddressShouldReturnFalse() {
        val result = deadQueueRepository.existsByWalletAddress("address").block()
        Assertions.assertThat(result).isEqualTo(false)
    }

    @Test
    fun existsByWalletAddressShouldReturnTrue() {
        val deadQueue = createDummyWebhookDeadQueue()
        deadQueueRepository.save(deadQueue).block()!!

        val result = deadQueueRepository.existsByWalletAddress("address").block()!!
        Assertions.assertThat(result).isEqualTo(true)
    }

    @Test
    fun deleteByWalletAddressShouldRemoveValue() {
        val deadQueue = createDummyWebhookDeadQueue()
        deadQueueRepository.save(deadQueue).block()!!

        deadQueueRepository.deleteByWalletAddress("address").block()

        val result = deadQueueRepository.existsByWalletAddress("address").block()!!
        Assertions.assertThat(result).isEqualTo(false)
    }
}
