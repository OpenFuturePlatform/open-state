package io.openfuture.state.repository

import io.openfuture.state.base.MongoRepositoryTests
import io.openfuture.state.util.createDummyTransaction
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class TransactionRepositoryTest: MongoRepositoryTests() {

    @Autowired
    private lateinit var transactionsRepository: TransactionRepository


    @AfterEach
    fun tearDown() {
        transactionsRepository.deleteAll().block()
    }

    @Test
    fun findByHashShouldReturnTransaction() {
        var transaction = createDummyTransaction()
        transaction = transactionsRepository.save(transaction).block()!!

        val result = transactionsRepository.findByHash("hash").block()!!
        Assertions.assertThat(result).isEqualTo(transaction)
    }

    @Test
    fun findByHashShouldReturnNull() {
        val result = transactionsRepository.findByHash("hash").block()
        Assertions.assertThat(result).isNull()
    }
}
