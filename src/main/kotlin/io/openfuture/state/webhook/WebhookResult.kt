package io.openfuture.state.webhook

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

data class WebhookResult(
        val status: HttpStatus,
        val url: String,
        val attempt: Int,
        val message: String? = null,
        val timestamp: LocalDateTime = LocalDateTime.now()
) {

    constructor(response: WebhookResponse, attempt: Int): this(
            response.status,
            response.url,
            attempt,
            response.message
    )
}
