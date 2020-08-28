package io.openfuture.state.controller.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ErrorDto(
        val status: Int,
        val reason: String?,
        val errors: List<FieldErrorDto> = listOf()
)
