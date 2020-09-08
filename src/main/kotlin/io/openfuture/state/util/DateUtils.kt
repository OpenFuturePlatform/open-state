package io.openfuture.state.util

import java.time.Instant
import java.time.LocalDateTime
import java.util.*

fun Long.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(this),
            TimeZone.getDefault().toZoneId());
}
