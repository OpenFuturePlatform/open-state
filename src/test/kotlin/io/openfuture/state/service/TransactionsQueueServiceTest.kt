package io.openfuture.state.service

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import io.openfuture.state.base.ServiceTests
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.TransactionsRedisRepository
import io.openfuture.state.util.JsonSerializer
import io.openfuture.state.util.createDummyScheduledTransaction
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

internal class TransactionsQueueServiceTest: ServiceTests() {

    private lateinit var service: TransactionsQueueService
    private val repository: TransactionsRedisRepository = mock()
    private val jsonSerializer: JsonSerializer = JsonSerializer()


    @BeforeEach
    fun setUp() {
        service = DefaultTransactionsQueueService(repository, jsonSerializer)
    }

    @Test
    fun hasTransactionsShoulReturnFalse() = runBlocking<Unit> {
        given(repository.count("walletKey")).willReturn(Mono.just(0))
        val result = service.hasTransactions("walletKey")

        Assertions.assertThat(result).isFalse()
    }

    @Test
    fun hasTransactionsShoulReturnTrue() = runBlocking<Unit> {
        given(repository.count("walletKey")).willReturn(Mono.just(5))
        val result = service.hasTransactions("walletKey")

        Assertions.assertThat(result).isTrue()
    }

    @Test
    fun firstShouldThrowNotFoundException() = runBlocking<Unit> {
        given(repository.first("walletKey")).willReturn(Mono.empty())
        assertThrows(NotFoundException::class.java) {
            runBlocking {
                service.first("walletKey")
            }
        }
    }

    @Test
    fun firstShouldReturnProperValue() = runBlocking<Unit> {
        val transaction = createDummyScheduledTransaction()
        val serializedTransaction = jsonSerializer.toJson(transaction)

        given(repository.first("walletKey")).willReturn(Mono.just(serializedTransaction))

        val result = service.first("walletKey")
        Assertions.assertThat(result).isEqualTo(transaction)
    }

    @Test
    fun findAllShouldReturnEmptyList() = runBlocking<Unit> {
        given(repository.count("walletKey")).willReturn(Mono.just(0))
        given(repository.findAll("walletKey", 0, 0)).willReturn(Flux.empty())

        val result = service.findAll("walletKey")
        Assertions.assertThat(result.size).isEqualTo(0)
    }

    @Test
    fun findAllShouldReturnListOfValues() = runBlocking<Unit> {
        val transaction1 = createDummyScheduledTransaction()
        val transaction2 = createDummyScheduledTransaction()

        given(repository.count("walletKey")).willReturn(Mono.just(2))
        given(repository.findAll("walletKey", 0, 2))
                .willReturn(Flux.just(
                        jsonSerializer.toJson(transaction1),
                        jsonSerializer.toJson(transaction2)
                ))

        val result = service.findAll("walletKey")
        Assertions.assertThat(result).isEqualTo(listOf(transaction1, transaction2))
    }
}
