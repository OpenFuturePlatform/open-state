package io.openfuture.state.repository

import com.mongodb.internal.connection.tlschannel.util.Util.assertTrue
import io.openfuture.state.base.RedisRepositoryTests
import io.openfuture.state.model.Blockchain
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class BlockchainRepositoryTest : RedisRepositoryTests() {

    private lateinit var blockchainRepository: BlockchainRepository

    @BeforeEach
    fun setUp() {
        blockchainRepository = BlockchainRepository(reactiveRedisTemplate, 100)
        reactiveRedisTemplate.execute {
            it.serverCommands().flushAll()
        }.blockFirst()
    }

    @Test
    fun getCurrentBlockNumberReturnZero() = runBlocking<Unit> {
        val result = blockchainRepository.getCurrentBlockNumber(Blockchain.ETHEREUM)
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun getLastBlockNumberReturnZero() = runBlocking<Unit> {
        val result = blockchainRepository.getLastBlockNumber(Blockchain.ETHEREUM)
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun setLastBlockNumberShouldSave() = runBlocking<Unit> {
        blockchainRepository.setLastBlockNumber(Blockchain.ETHEREUM, 55)

        val result = blockchainRepository.getLastBlockNumber(Blockchain.ETHEREUM)

        assertThat(result).isEqualTo(55)
    }

    @Test
    fun incrementCurrentBlockNumberShouldReturnSequence() = runBlocking<Unit> {
        assertThat(blockchainRepository.incrementCurrentBlockNumber(Blockchain.ETHEREUM)).isEqualTo(1)
        assertThat(blockchainRepository.incrementCurrentBlockNumber(Blockchain.ETHEREUM)).isEqualTo(2)
        assertThat(blockchainRepository.incrementCurrentBlockNumber(Blockchain.ETHEREUM)).isEqualTo(3)
    }

    @Test
    fun lockReturnTrue() = runBlocking {
        reactiveRedisTemplate.opsForValue().set("lock:${Blockchain.ETHEREUM}", LocalDateTime.now())
        val result = blockchainRepository.lock(Blockchain.ETHEREUM)
        assertTrue(result)
    }

    @Test
    fun lockReturnFalse() = runBlocking {
        reactiveRedisTemplate.opsForValue().set("lock:${Blockchain.OPEN_CHAIN}", LocalDateTime.now())
        val result = blockchainRepository.lock(Blockchain.ETHEREUM)
        assertTrue(result)
    }
}
