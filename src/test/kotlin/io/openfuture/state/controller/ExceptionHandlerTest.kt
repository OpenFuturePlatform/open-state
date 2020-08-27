package io.openfuture.state.controller

import io.openfuture.state.exception.NotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class ExceptionHandlerTest {

    private val exceptionHandler = ExceptionHandler()

    @Test
    fun handleNotFoundExceptionTest() {
        val exception = NotFoundException("not found")
        val errorDto = ExceptionHandler.ErrorDto(HttpStatus.NOT_FOUND.value(), exception.message)

        val result = exceptionHandler.handleNotFoundException(exception)
        assertThat(result).isEqualTo(errorDto)

    }

}
