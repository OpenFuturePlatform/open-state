package io.openfuture.state.service

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import io.openfuture.state.base.ServiceTests
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.TransactionsRedisRepository
import io.openfuture.state.util.JsonUtil
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


    @BeforeEach
    fun setUp() {
        service = DefaultTransactionsQueueService(repository)
    }

    @Test
    fun hasTransactionsShoulReturnFalse() = runBlocking<Unit> {
        given(repository.count("address")).willReturn(Mono.just(0))
        val result = service.hasTransactions("address")

        Assertions.assertThat(result).isFalse()
    }

    @Test
    fun hasTransactionsShoulReturnTrue() = runBlocking<Unit> {
        given(repository.count("address")).willReturn(Mono.just(5))
        val result = service.hasTransactions("address")

        Assertions.assertThat(result).isTrue()
    }

    @Test
    fun firstTransactionShouldThrowNotFoundException() = runBlocking<Unit> {
        given(repository.first("address")).willReturn(Mono.empty())
        assertThrows(NotFoundException::class.java) {
            runBlocking {
                service.firstTransaction("address")
            }
        }
    }

    @Test
    fun firstTransactionShouldReturnProperValue() = runBlocking<Unit> {
        val transaction = createDummyScheduledTransaction()
        val serializedTransaction = JsonUtil.toJson(transaction)

        given(repository.first("address")).willReturn(Mono.just(serializedTransaction))

        val result = service.firstTransaction("address")
        Assertions.assertThat(result).isEqualTo(transaction)
    }

    @Test
    fun findAllShouldReturnEmptyList() = runBlocking<Unit> {
        given(repository.count("address")).willReturn(Mono.just(0))
        given(repository.findAll("address", 0, 0)).willReturn(Flux.empty())

        val result = service.findAll("address")
        Assertions.assertThat(result.size).isEqualTo(0)
    }

    @Test
    fun findAllShouldReturnListOfValues() = runBlocking<Unit> {
        val transaction1 = createDummyScheduledTransaction()
        val transaction2 = createDummyScheduledTransaction()

        given(repository.count("address")).willReturn(Mono.just(2))
        given(repository.findAll("address", 0, 2))
                .willReturn(Flux.just(
                        JsonUtil.toJson(transaction1),
                        JsonUtil.toJson(transaction2)
                ))

        val result = service.findAll("address")
        Assertions.assertThat(result).isEqualTo(listOf(transaction1, transaction2))
    }
}
