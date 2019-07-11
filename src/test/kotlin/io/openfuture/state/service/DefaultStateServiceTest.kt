package io.openfuture.state.service

import io.openfuture.state.exception.NotFoundException
import io.openfuture.state.repository.StateRepository
import io.openfuture.state.util.createDummyState
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import java.util.*

class DefaultStateServiceTest {

    private val repository = mock(StateRepository::class.java)

    private lateinit var stateService: StateService


    @Before
    fun setUp() {
        stateService = DefaultStateService(repository)
    }

    @Test
    fun saveShouldAddNewState() {
        val state = createDummyState()

        given(repository.save(state)).willReturn(state)

        val result = stateService.save(state)

        assertThat(result).isEqualTo(state)
    }

    @Test
    fun getShouldReturnStateWhenExists() {
        val state = createDummyState().apply { id = 1 }

        given(repository.findById(state.id)).willReturn(Optional.of(state))

        val result = stateService.get(state.id)

        assertThat(result).isEqualTo(state)
    }

    @Test(expected = NotFoundException::class)
    fun getShouldThrowExceptionWhenStateDoesNotExists() {
        val state = createDummyState().apply { id = 1 }

        given(repository.findById(state.id)).willReturn(Optional.empty())

        stateService.get(state.id)
    }

}
