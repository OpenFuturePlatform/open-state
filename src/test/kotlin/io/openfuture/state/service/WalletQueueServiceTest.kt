package io.openfuture.state.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import io.openfuture.state.base.ServiceTests
import io.openfuture.state.repository.WalletQueueRedisRepository
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import java.time.LocalDateTime

internal class WalletQueueServiceTest: ServiceTests() {

    private lateinit var service: WalletQueueService
    private val repository: WalletQueueRedisRepository = mock()


    @BeforeEach
    fun setUp() {
        service = DefaultWalletQueueService(repository)
    }

    @Test
    fun walletsScheduledToShouldReturnEmptyList() = runBlocking<Unit> {
        given(repository.walletsScheduledTo(any())).willReturn(Flux.empty())
        val result = service.walletsScheduledTo(LocalDateTime.now())

        Assertions.assertThat(result.size).isEqualTo(0)
    }

    @Test
    fun walletsScheduledToShouldReturnProperValues() = runBlocking<Unit> {
        given(repository.walletsScheduledTo(any())).willReturn(Flux.just("address1", "address2", "address3"))
        val result = service.walletsScheduledTo(LocalDateTime.now())

        Assertions.assertThat(result).isEqualTo(listOf("address1", "address2", "address3"))
    }
}
