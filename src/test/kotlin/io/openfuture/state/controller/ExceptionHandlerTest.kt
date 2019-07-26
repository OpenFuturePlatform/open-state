package io.openfuture.state.controller

import io.openfuture.state.controller.domain.dto.ErrorDto
import io.openfuture.state.exception.NotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
class ExceptionHandlerTest {

    private val exceptionHandler = ExceptionHandler()


    @Test
    fun handleNotFoundExceptionTest() {
        val exception = NotFoundException("not found")
        val errorDto = ErrorDto(HttpStatus.NOT_FOUND.value(), exception.message)

        val result = exceptionHandler.handleNotFoundException(exception)
        assertThat(result).isEqualTo(errorDto)

    }

}
