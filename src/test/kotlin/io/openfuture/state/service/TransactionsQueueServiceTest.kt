package io.openfuture.state.service

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import io.openfuture.state.base.ServiceTests
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.TransactionQueueRedisRepository
import io.openfuture.state.util.createDummyTransactionQueueTask
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono

internal class TransactionsQueueServiceTest: ServiceTests() {

    private lateinit var service: TransactionsQueueService
    private val repository: TransactionQueueRedisRepository = mock()


    @BeforeEach
    fun setUp() {
        service = DefaultTransactionsQueueService(repository)
    }

    @Test
    fun firstShouldThrowNotFoundException() = runBlocking<Unit> {
        given(repository.first("walletId")).willReturn(Mono.empty())
        assertThrows(NotFoundException::class.java) {
            runBlocking {
                service.first("walletId")
            }
        }
    }

    @Test
    fun firstShouldReturnProperValue() = runBlocking<Unit> {
        val transactionTask = createDummyTransactionQueueTask()

        given(repository.first("walletId")).willReturn(Mono.just(transactionTask))

        val result = service.first("walletId")
        Assertions.assertThat(result).isEqualTo(transactionTask)
    }

    @Test
    fun hasTransactionsShouldReturnFalse() = runBlocking<Unit> {
        given(repository.count("walletId")).willReturn(Mono.just(0))
        val result = service.hasTransactions("walletId")

        Assertions.assertThat(result).isFalse()
    }

    @Test
    fun hasTransactionsShouldReturnTrue() = runBlocking<Unit> {
        given(repository.count("walletId")).willReturn(Mono.just(5))
        val result = service.hasTransactions("walletId")

        Assertions.assertThat(result).isTrue()
    }
}
