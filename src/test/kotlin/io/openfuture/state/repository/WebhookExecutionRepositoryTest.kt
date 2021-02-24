package io.openfuture.state.repository

import io.openfuture.state.base.MongoRepositoryTests
import io.openfuture.state.util.createDummyWebhookExecution
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class WebhookExecutionRepositoryTest : MongoRepositoryTests() {

    @Autowired
    private lateinit var executionRepository: WebhookExecutionRepository


    @AfterEach
    fun tearDown() {
        executionRepository.deleteAll().block()
    }

    @Test
    fun findByTransactionHashShouldReturnWebhookExecution() {
        var execution = createDummyWebhookExecution()
        execution = executionRepository.save(execution).block()!!

        val result = executionRepository.findByTransactionHash("hash").block()!!
        assertThat(result).isEqualTo(execution)
    }

    @Test
    fun findByBlockchainAndAddressShouldReturnNull() {
        val result = executionRepository.findByTransactionHash("hash").block()
        assertThat(result).isNull()
    }
}
