package io.openfuture.state.service

import io.openfuture.state.controller.domain.request.SaveOpenScaffoldRequest
import io.openfuture.state.entity.OpenScaffold
import io.openfuture.state.exception.DuplicateEntityException
import io.openfuture.state.repository.ScaffoldRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito

internal class DefaultOpenScaffoldServiceTest {

    private val repository = Mockito.mock(ScaffoldRepository::class.java)
    private lateinit var service: DefaultOpenScaffoldService

    @Before
    fun setUp() {
        service = DefaultOpenScaffoldService(repository)
    }

    @Test
    fun findByRecipientAddress_ShouldReturnScaffold() {
        val address = "address"
        val expected = OpenScaffold(
                recipientAddress = address,
                webHook = "https://url.org"
        )
        given(repository.findByRecipientAddress("address")).willReturn(expected)

        val actual: OpenScaffold? = service.findByRecipientAddress(address)

        assertEquals(actual, expected)
    }

    @Test(expected = DuplicateEntityException::class)
    fun save_WhenScaffoldWithSuchAddressExist_ShouldThrowDuplicateEntityException() {
        val request = SaveOpenScaffoldRequest("address", "http://webhook.io")

        given(repository.findByRecipientAddress(request.address))
                .willReturn(OpenScaffold(request.address, request.webHook))

        service.save(request)
    }

    @Test
    fun save_WhenScaffoldIsNotExisting_ShouldSaveOpenScaffold() {
        val request = SaveOpenScaffoldRequest("address", "http://webhook.io")
        val expected = OpenScaffold(request.address, request.webHook)

        given(repository.findByRecipientAddress(request.address))
                .willReturn(null)
        given(repository.save(expected))
                .willReturn(expected)

        val actual = service.save(request)

        assertEquals(expected, actual)
    }

}