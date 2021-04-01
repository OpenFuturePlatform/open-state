package io.openfuture.state.extensions

import io.openfuture.state.util.toEpochMilli
import org.springframework.data.redis.core.ReactiveRedisTemplate
import java.nio.ByteBuffer
import java.time.LocalDateTime

fun ReactiveRedisTemplate<String, Any>.keyToByteBuffer(key: String): ByteBuffer {
    return this.serializationContext.keySerializationPair.write(key)
}

fun ReactiveRedisTemplate<String, Any>.valueToByteBuffer(value: Any): ByteBuffer {
    return this.serializationContext.valueSerializationPair.write(value)
}

fun LocalDateTime.toMillisDouble(): Double {
    return this.toEpochMilli().toDouble()
}
