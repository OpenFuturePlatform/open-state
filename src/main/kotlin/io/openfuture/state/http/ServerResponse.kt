package io.openfuture.state.http

import org.springframework.http.HttpStatus

data class ServerResponse(
    val status: HttpStatus,
    val url: String,
    val message: String?
)
