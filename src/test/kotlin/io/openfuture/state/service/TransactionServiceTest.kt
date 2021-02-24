package io.openfuture.state.service

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import io.openfuture.state.base.ServiceTests
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.TransactionRepository
import io.openfuture.state.util.createDummyTransaction
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono

internal class TransactionServiceTest: ServiceTests() {

    private lateinit var service: TransactionService
    private val repository: TransactionRepository = mock()


    @BeforeEach
    fun setUp() {
        service = DefaultTransactionService(repository)
    }

    @Test
    fun findByHashShouldThrowNotFoundException() {
        given(repository.findByHash("hash")).willReturn(Mono.empty())
        org.junit.jupiter.api.Assertions.assertThrows(NotFoundException::class.java) {
            runBlocking {
                service.findByHash("hash")
            }
        }
    }

    @Test
    fun findByHashShouldReturnTransaction() = runBlocking<Unit> {
        val transaction = createDummyTransaction()

        given(repository.findByHash("hash")).willReturn(Mono.just(transaction))
        val result = service.findByHash("hash")

        Assertions.assertThat(result).isEqualTo(transaction)
    }
}
