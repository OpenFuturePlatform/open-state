package io.openfuture.state.service

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import io.openfuture.state.base.ServiceTests
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.TransactionRepository
import io.openfuture.state.util.createDummyTransaction
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono

internal class TransactionServiceTest : ServiceTests() {

    private lateinit var transactionService: TransactionService
    private val repository: TransactionRepository = mock()


    @BeforeEach
    fun setUp() {
        transactionService = DefaultTransactionService(repository)
    }

    @Test
    fun findByIdShouldThrowNotFoundException() {
        given(repository.findById("transactionId")).willReturn(Mono.empty())
        Assertions.assertThrows(NotFoundException::class.java) {
            runBlocking {
                transactionService.findById("transactionId")
            }
        }
    }

    @Test
    fun findByIdShouldReturnTransaction() = runBlocking<Unit> {
        val transaction = createDummyTransaction()

        given(repository.findById("transactionId")).willReturn(Mono.just(transaction))

        val result = transactionService.findById("transactionId")

        org.assertj.core.api.Assertions.assertThat(result).isEqualTo(transaction)
    }

}
