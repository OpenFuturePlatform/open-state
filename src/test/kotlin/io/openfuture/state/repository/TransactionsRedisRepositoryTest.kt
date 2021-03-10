package io.openfuture.state.repository

import io.openfuture.state.base.RedisRepositoryTests
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TransactionsRedisRepositoryTest: RedisRepositoryTests() {

    private lateinit var repository: TransactionsRedisRepository


    @BeforeEach
    fun setUp() {
        repository = TransactionsRedisRepository(redisTemplate)
        redisTemplate.execute {
            it.serverCommands().flushAll()
        }.blockFirst()
    }

    @Test
    fun countShouldReturnZero() = runBlocking<Unit> {
        val result = repository.count("address").awaitSingle()
        Assertions.assertThat(result).isEqualTo(0)
    }

    @Test
    fun countShouldReturnProperValue() = runBlocking<Unit> {
        redisTemplate.opsForList().rightPush("address", "value").awaitSingle()

        val result = repository.count("address").awaitSingle()
        Assertions.assertThat(result).isEqualTo(1)
    }

    @Test
    fun firstShouldReturnNull() = runBlocking<Unit> {
        val result = repository.first("address").awaitFirstOrNull()
        Assertions.assertThat(result).isNull()
    }

    @Test
    fun firstShouldReturnProperValue() = runBlocking<Unit> {
        redisTemplate.opsForList().rightPush("address", "value").awaitSingle()

        val result = repository.first("address").awaitSingle()
        Assertions.assertThat(result).isEqualTo("value")
    }

    @Test
    fun firstShouldReturnFirstValueInList() = runBlocking<Unit> {
        redisTemplate.opsForList().rightPush("address", "value1").awaitSingle()
        redisTemplate.opsForList().rightPush("address", "value2").awaitSingle()
        redisTemplate.opsForList().rightPush("address", "value3").awaitSingle()

        val result = repository.first("address").awaitSingle()
        Assertions.assertThat(result).isEqualTo("value1")
    }

    @Test
    fun findAllShouldReturnEmptyList() = runBlocking<Unit> {
        val result = repository.findAll("address", 0, 5).collectList().awaitSingle()
        Assertions.assertThat(result.size).isEqualTo(0)
    }

    @Test
    fun findAllShouldReturnProperListOfValues() = runBlocking<Unit> {
        redisTemplate.opsForList().rightPush("address", "value1").awaitSingle()
        redisTemplate.opsForList().rightPush("address", "value2").awaitSingle()
        redisTemplate.opsForList().rightPush("address", "value3").awaitSingle()

        val result = repository.findAll("address", 0, 2).collectList().awaitSingle()
        Assertions.assertThat(result).isEqualTo(listOf("value1", "value2", "value3"))
    }

    @Test
    fun addShouldAddElement() = runBlocking<Unit> {
        repository.add("address", "value1")

        val result = redisTemplate.opsForList().range("address", 0, 1).collectList().awaitSingle()
        Assertions.assertThat(result).isEqualTo(listOf("value1"))
    }

    @Test
    fun addShouldAddElementToEnd() = runBlocking<Unit> {
        repository.add("address", "value1")
        repository.add("address", "value2")

        val result = redisTemplate.opsForList().range("address", 0, 1).collectList().awaitSingle()
        Assertions.assertThat(result).isEqualTo(listOf("value1", "value2"))
    }

    @Test
    fun setAtPositionShouldAddElementInProperIndex() = runBlocking<Unit> {
        redisTemplate.opsForList().rightPush("address", "value1").awaitSingle()
        redisTemplate.opsForList().rightPush("address", "value2").awaitSingle()
        redisTemplate.opsForList().rightPush("address", "value3").awaitSingle()

        repository.setAtPosition("address", "value12", 1)

        val result = redisTemplate.opsForList().range("address", 1, 1).collectList().awaitSingle()
        Assertions.assertThat(result).isEqualTo(listOf("value12"))
    }

    @Test
    fun removeShouldRemoveListOfElements() = runBlocking<Unit> {
        redisTemplate.opsForList().rightPush("address", "value1").awaitSingle()

        repository.remove("address")

        val result = redisTemplate.opsForList().size("address").awaitSingle()
        Assertions.assertThat(result).isEqualTo(0)
    }
}
