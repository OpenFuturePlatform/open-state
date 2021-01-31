package io.openfuture.state.webhook

import org.springframework.http.HttpStatus

data class WebhookResponse(val status: HttpStatus, val message: String? = null)
