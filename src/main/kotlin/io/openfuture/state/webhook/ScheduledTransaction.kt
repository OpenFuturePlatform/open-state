package io.openfuture.state.webhook

import java.time.LocalDateTime

data class ScheduledTransaction(
        val id: String = "",
        var attempts: Int = 1,
        val timestamp: LocalDateTime = LocalDateTime.now()
)
