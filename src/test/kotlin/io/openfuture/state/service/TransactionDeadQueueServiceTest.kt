package io.openfuture.state.service

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import io.openfuture.state.base.ServiceTests
import io.openfuture.state.domain.WalletIdentity
import io.openfuture.state.repository.TransactionDeadQueueRepository
import io.openfuture.state.util.createDummyTransactionDeadQueue
import io.openfuture.state.util.createDummyTransactionQueueTask
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono

internal class TransactionDeadQueueServiceTest: ServiceTests() {

    private lateinit var service: TransactionDeadQueueService
    private val repository: TransactionDeadQueueRepository = mock()


    @BeforeEach
    fun setUp() {
        service = DefaultTransactionDeadQueueService(repository)
    }

    @Test
    fun addTransactionToDeadQueueShouldAddTransactionToNewQueue() = runBlocking<Unit> {
        val walletIdentity = WalletIdentity(blockchain = "Ethereum", address = "address")
        val transactionTask = createDummyTransactionQueueTask()

        given(repository.findByWalletIdentity(walletIdentity)).willReturn(Mono.empty())

        val result = service.addTransactionToDeadQueue(walletIdentity, listOf(transactionTask))

        assertThat(result.getTransactions()).isEqualTo(listOf(transactionTask))
    }

    @Test
    fun addTransactionToDeadQueueShouldAddTransactionToExistingQueue() = runBlocking<Unit> {
        val transactionTask1 = createDummyTransactionQueueTask(transactionId = "transactionId1")
        val transactionTask2 = createDummyTransactionQueueTask(transactionId = "transactionId2")
        val transactionDeadQueue = createDummyTransactionDeadQueue(transactions = mutableListOf(transactionTask1))

        given(repository.findByWalletIdentity(transactionDeadQueue.walletIdentity))
            .willReturn(Mono.just(transactionDeadQueue))

        val result = service.addTransactionToDeadQueue(transactionDeadQueue.walletIdentity, listOf(transactionTask2))

        assertThat(result.getTransactions()).isEqualTo(listOf(transactionTask1, transactionTask2))
    }

    @Test
    fun getTransactionFromDeadQueueShouldReturnEmptyList() = runBlocking<Unit> {
        val walletIdentity = WalletIdentity(blockchain = "Ethereum", address = "address")
        given(repository.existsByWalletIdentity(walletIdentity)).willReturn(Mono.just(false))

        val result = service.getTransactionFromDeadQueue(walletIdentity)

        assertThat(result.size).isEqualTo(0)
    }

    @Test
    fun getTransactionFromDeadQueueShouldReturnListOfValues() = runBlocking<Unit> {
        val walletIdentity = WalletIdentity(blockchain = "Ethereum", address = "address")
        val transactionTask = createDummyTransactionQueueTask()
        val transactionDeadQueue = createDummyTransactionDeadQueue(identity = walletIdentity, transactions = mutableListOf(transactionTask))

        given(repository.existsByWalletIdentity(walletIdentity)).willReturn(Mono.just(true))
        given(repository.findByWalletIdentity(walletIdentity)).willReturn(Mono.just(transactionDeadQueue))

        val result = service.getTransactionFromDeadQueue(walletIdentity)

        assertThat(result).isEqualTo(listOf(transactionTask))
    }

}
