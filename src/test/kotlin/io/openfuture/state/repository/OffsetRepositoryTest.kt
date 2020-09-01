package io.openfuture.state.repository

import io.openfuture.state.base.RedisRepositoryTests
import io.openfuture.state.model.Blockchain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OffsetRepositoryTest : RedisRepositoryTests() {

    private lateinit var offsetRepository: OffsetRepository

    @BeforeEach
    fun setUp() {
        offsetRepository = OffsetRepository(reactiveRedisTemplate)
        reactiveRedisTemplate.execute {
            it.serverCommands().flushAll()
        }.blockFirst()
    }

    @Test
    fun getCurrentShouldReturnOne() {
        val currentPageNumber = offsetRepository.getCurrent(Blockchain.ETHEREUM).block()
        assertThat(currentPageNumber).isEqualTo(0)
    }

    @Test
    fun incrementShouldReturnSequence() {
        assertThat(offsetRepository.increment(Blockchain.ETHEREUM).block()).isEqualTo(1)
        assertThat(offsetRepository.increment(Blockchain.ETHEREUM).block()).isEqualTo(2)
        assertThat(offsetRepository.increment(Blockchain.ETHEREUM).block()).isEqualTo(3)
    }
}
