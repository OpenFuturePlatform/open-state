package io.openfuture.state.controller

import io.openfuture.state.controller.domain.dto.ErrorDto
import io.openfuture.state.controller.domain.dto.FieldErrorDto
import io.openfuture.state.exception.DuplicateEntityException
import io.openfuture.state.exception.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException): ErrorDto {
        return ErrorDto(HttpStatus.NOT_FOUND.value(), ex.message)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleException(ex: MethodArgumentNotValidException): ErrorDto {
        val fieldErrors = ex.bindingResult.fieldErrors.map { FieldErrorDto(it.field, it.defaultMessage) }
        return ErrorDto(HttpStatus.BAD_REQUEST.value(), "Invalid parameters", fieldErrors)
    }


    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(DuplicateEntityException::class)
    fun handleDuplicateException(ex: DuplicateEntityException): ErrorDto {
        return ErrorDto(HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.message)
    }

}
