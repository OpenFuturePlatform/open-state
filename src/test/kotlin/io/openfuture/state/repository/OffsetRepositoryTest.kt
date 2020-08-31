package io.openfuture.state.repository

import io.openfuture.state.base.RedisRepositoryTests
import io.openfuture.state.model.BlockchainType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisTemplate

class OffsetRepositoryTest : RedisRepositoryTests() {

    @Autowired
    private lateinit var offsetRedisTemplate: ReactiveRedisTemplate<BlockchainType, Long>

    private lateinit var offsetRepository: OffsetRepository

    @BeforeEach
    fun setUp() {
        offsetRepository = OffsetRepository(offsetRedisTemplate)
        offsetRedisTemplate.execute {
            it.serverCommands().flushAll()
        }.blockFirst()
    }

    @Test
    fun getCurrentShouldReturnOne() {
        val currentPageNumber = offsetRepository.getCurrent(BlockchainType.ETHEREUM).block()
        assertThat(currentPageNumber).isEqualTo(0)
    }

    @Test
    fun incrementShouldReturnSequence() {
        assertThat(offsetRepository.increment(BlockchainType.ETHEREUM).block()).isEqualTo(1)
        assertThat(offsetRepository.increment(BlockchainType.ETHEREUM).block()).isEqualTo(2)
        assertThat(offsetRepository.increment(BlockchainType.ETHEREUM).block()).isEqualTo(3)
    }
}
