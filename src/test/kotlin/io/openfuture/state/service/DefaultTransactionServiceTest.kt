package io.openfuture.state.service

import io.openfuture.state.domain.page.PageRequest
import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.TransactionRepository
import io.openfuture.state.util.createDummyTransaction
import io.openfuture.state.util.createDummyWallet
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.springframework.data.domain.PageImpl

class DefaultTransactionServiceTest {

    private val repository = mock(TransactionRepository::class.java)

    private lateinit var transactionService: TransactionService


    @Before
    fun setUp() {
        transactionService = DefaultTransactionService(repository)
    }

    @Test
    fun saveShouldAddNewTransaction() {
        val transaction = createDummyTransaction()

        given(repository.save(transaction)).willReturn(transaction)

        val result = transactionService.save(transaction)

        assertThat(result).isEqualTo(transaction)
    }

    @Test
    fun getShouldReturnTransactionWhenExists() {
        val wallet = createDummyWallet().apply { id = 1 }
        val transaction = createDummyTransaction(wallet).apply { id = 1 }

        given(repository.findByIdAndWalletId(transaction.id, wallet.id)).willReturn(transaction)

        val result = transactionService.get(transaction.id, wallet.id)

        assertThat(result).isEqualTo(transaction)
    }

    @Test(expected = NotFoundException::class)
    fun getShouldThrowExceptionWhenTransactionDoesNotExists() {
        val wallet = createDummyWallet().apply { id = 1 }
        val transaction = createDummyTransaction(wallet).apply { id = 1 }

        given(repository.findByIdAndWalletId(transaction.id, wallet.id)).willReturn(null)

        transactionService.get(transaction.id, wallet.id)
    }

    @Test
    fun getAllByWalletIdShouldFetchAllWalletTransactions() {
        val pageRequest = PageRequest()
        val wallet = createDummyWallet().apply { id = 1 }
        val transactions = listOf(createDummyTransaction(wallet))

        given(repository.findAllByWalletIdOrderByDateDesc(wallet.id, pageRequest)).willReturn(PageImpl(transactions))

        val result = transactionService.getAllByWalletId(wallet.id, pageRequest)

        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content).isEqualTo(transactions)
    }

}
